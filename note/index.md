---
layout: note
---

<h1>Learning Notes</h1>
<!-- This loops through the paginated posts -->
{% for post in site.posts %}
<article>
  {{ content }}
</article>
<section class="meta">
  <span class="author">
    <a href="https://github.com/elan2wang">{{ site.author }}</a>
  </span>
  <span class="time">
    /<time datetime="{{ page.date | date:"%Y-%m-%d" }}">{{ page.date | date:"%Y-%m-%d" }}</time>
  </span>
</section>
{% endfor %} 

<!-- Pagination links -->
<div class="pagination">
  {% if paginator.next_page %}
  <a class="prev" href="{{paginator.next_page}}">&larr; Older</a>
  {% endif %}
  {% if paginator.previous_page %}
  <a class="prev" href="{{paginator.next_page}}">&larr; Older</a>
  {% endif %}
</div>