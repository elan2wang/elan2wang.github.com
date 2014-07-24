/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.scenario.effect.impl;

import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sun.scenario.effect.Filterable;

/**
 * A simple object pool used to recycle temporary images used by the
 * various {@code EffectPeer} implementations.  Image allocation can be
 * a fairly expensive operation (in terms of footprint and performance),
 * especially for the GPU backends, so image reuse is critical.
 */
public class ImagePool {

    public static long numEffects;
    static long numCreated;
    static long pixelsCreated;
    static long numAccessed;
    static long pixelsAccessed;

    static {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                if (System.getProperty("decora.showstats") != null) {
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override public void run() {
                            printStats();
                        }
                    });
                }
                return null;
            }
        });
    }

    static void printStats() {
        System.out.println("effects executed:  " + numEffects);
        System.out.println("images created:    " + numCreated);
        System.out.println("pixels created:    " + pixelsCreated);
        System.out.println("images accessed:   " + numAccessed);
        System.out.println("pixels accessed:   " + pixelsAccessed);
        if (numEffects != 0) { 
            double avgImgs = ((double) numAccessed) / numEffects;
            double avgPxls = ((double) pixelsAccessed) / numEffects;
            System.out.println("images per effect: " + avgImgs);
            System.out.println("pixels per effect: " + avgPxls);
        }
    }

    static final int QUANT = 32;

    private final List<SoftReference<Filterable>> unlocked =
        new ArrayList<SoftReference<Filterable>>();
    private final List<SoftReference<Filterable>> locked =
        new ArrayList<SoftReference<Filterable>>();

    // On Canmore with the PowerVR SGX chip, there is a driver issue
    // that causes incorrect rendering if one tries to reuse an FBO
    // more than once in a particular frame (due to their tile-based
    // deferred rendering engine).  The ugly workaround here is to
    // avoid using the same Filterable (FBO) more than once between
    // swapBuffers() operations.  When the workaround is enabled,
    // the checkIn() method will move the Filterable into "purgatory"
    // instead of returning it to the pool of available images.  Just
    // after the swapBuffers() operation, the Prism toolkit will call
    // the releasePurgatory() method to allow images to return to the
    // pool for the next rendering cycle.  This of course greatly
    // increases the amount of VRAM used by an app, and may cause
    // slowdowns for certain frames due to increased allocation
    // (where there would normally be reuse).
    private final boolean usePurgatory = Boolean.getBoolean("decora.purgatory");
    private final List<Filterable> hardPurgatory = new ArrayList<Filterable>();
    private final List<SoftReference<Filterable>> softPurgatory =
        new ArrayList<SoftReference<Filterable>>();

    /**
     * Package-private constructor.
     */
    ImagePool() {
    }

    public synchronized Filterable checkOut(Renderer renderer, int w, int h) {
        if (w <= 0 || h <= 0) {
            // if image is empty in any way, return a small non-empty image.
            w = h = 1;
        }
        // Allocate images rounded up to the nearest quantum size threshold.
        w = ((w + QUANT - 1) / QUANT) * QUANT;
        h = ((h + QUANT - 1) / QUANT) * QUANT;

        // Adjust allocation sizes for platform requirements (pow2 etc.)
        w = renderer.getCompatibleWidth(w);
        h = renderer.getCompatibleHeight(h);

        numAccessed++;
        pixelsAccessed += ((long) w) * h;
        // first look for an already cached image of sufficient size,
        // choosing the one that is closest in size to the requested dimensions
        SoftReference<Filterable> chosenEntry = null;
        Filterable chosenImage = null;
        int mindiff = Integer.MAX_VALUE;
        Iterator<SoftReference<Filterable>> entries = unlocked.iterator();
        while (entries.hasNext()) {
            SoftReference<Filterable> entry = entries.next();
            Filterable eimg = entry.get();
            if (eimg == null) {
                entries.remove();
                continue;
            }
            int ew = eimg.getMaxContentWidth();
            int eh = eimg.getMaxContentHeight();
            if (ew >= w && eh >= h && ew * eh / 2 <= w * h) {
                int diff = (ew-w) * (eh-h);
                if (chosenEntry == null || diff < mindiff) {
                    eimg.lock();
                    if (eimg.isLost()) {
                        entries.remove();
                        continue;
                    }
                    if (chosenImage != null) {
                        chosenImage.unlock();
                    }
                    chosenEntry = entry;
                    // The following calls to setContentWidth / setContentHeight
                    // should be uncommented only after the rest of the imagepool
                    // is fixed to handle a change in content size, and when both the
                    // SW pipeline and J2D pipeline are able to handle the change.
//                    eimg.setContentWidth(w);
//                    eimg.setContentHeight(h);
                    chosenImage = eimg;
                    mindiff = diff;
                }
            }
        }

        if (chosenEntry != null) {
            unlocked.remove(chosenEntry);
            locked.add(chosenEntry);
            renderer.clearImage(chosenImage);
            return chosenImage;
        }

        // get rid of expired entries from locked list
        entries = locked.iterator();
        while (entries.hasNext()) {
            SoftReference<Filterable> entry = entries.next();
            Filterable eimg = entry.get();
            if (eimg == null) {
                entries.remove();
            }
        }

        // if all else fails, just create a new one...
        Filterable img = null;
        try {
            img = renderer.createCompatibleImage(w, h);
        } catch (OutOfMemoryError e) {}

        if (img == null) {
            // we may be out of vram or heap
            pruneCache();
            try {
                img = renderer.createCompatibleImage(w, h);
            } catch (OutOfMemoryError e) {}
        }
        if (img != null) {
            locked.add(new SoftReference<Filterable>(img));
            numCreated++;
            pixelsCreated += ((long) w) * h;
        }
        return img;
    }

    public synchronized void checkIn(Filterable img) {
        SoftReference<Filterable> chosenEntry = null;
        Filterable chosenImage = null;
        Iterator<SoftReference<Filterable>> entries = locked.iterator();
        while (entries.hasNext()) {
            SoftReference<Filterable> entry = entries.next();
            Filterable eimg = entry.get();
            if (eimg == null) {
                entries.remove();
            } else if (eimg == img) {
                chosenEntry = entry;
                chosenImage = eimg;
                img.unlock();
                break;
            }
        }

        if (chosenEntry != null) {
            locked.remove(chosenEntry);
            if (usePurgatory) {
                // hold the entry in purgatory instead of releasing it back
                // to the unlocked pool immediately; it will be released
                // after the next call to releasePurgatory()...
//                System.err.println("==> Adding image to purgatory: " +
//                    chosenImage.getPhysicalWidth() + "x" +
//                    chosenImage.getPhysicalHeight());
                hardPurgatory.add(chosenImage);
                softPurgatory.add(chosenEntry);
            } else {
                unlocked.add(chosenEntry);
            }
        }
    }

    public synchronized void releasePurgatory() {
        if (usePurgatory && !softPurgatory.isEmpty()) {
//            System.err.println("==> Releasing " + softPurgatory.size() + " entries from purgatory!");
            // release images kept in purgatory back into the unlocked pool
            unlocked.addAll(softPurgatory);
            softPurgatory.clear();
            hardPurgatory.clear();
        }
    }

    private void pruneCache() {
        // flush all unlocked images
        for (SoftReference<Filterable> r : unlocked) {
            Filterable image = r.get();
            if (image != null) {
                image.flush();
            }
        }
        unlocked.clear();
        // this is to help to free up space held by those images that we no
        // longer have references to
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();
    }

    public synchronized void dispose() {
        for (SoftReference<Filterable> r : unlocked) {
            Filterable image = r.get();
            if (image != null) {
                image.flush();
            }
        }
        unlocked.clear();
        // not flushing the locked ones, just clearing references to them
        locked.clear();
    }
}
