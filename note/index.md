---
layout: note
---

<!-- This loops through the paginated posts -->
{% for post in paginator.posts %}
<article>
{% include article.html %}
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