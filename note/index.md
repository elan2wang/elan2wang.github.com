---
layout: note
---

<h1>Learning Notes</h1>
<!-- This loops through the paginated posts -->
{% for post in site.posts %}
<article>
  {{ content }}
</article>
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