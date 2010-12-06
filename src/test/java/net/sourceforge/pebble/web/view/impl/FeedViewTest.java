package net.sourceforge.pebble.web.view.impl;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import net.sourceforge.pebble.Constants;
import net.sourceforge.pebble.domain.BlogEntry;
import net.sourceforge.pebble.domain.Category;
import net.sourceforge.pebble.domain.SingleBlogTestCase;
import net.sourceforge.pebble.mock.MockHttpServletRequest;
import net.sourceforge.pebble.mock.MockHttpServletResponse;
import net.sourceforge.pebble.web.model.Model;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;

/**
 * @author James Roper
 */
public class FeedViewTest extends SingleBlogTestCase {

  public void testGetFeed() throws Exception {
    FeedView feedView = new FeedView(AbstractRomeFeedView.FeedType.ATOM);
    Model model = new Model();
    feedView.setModel(model);

    BlogEntry entry = new BlogEntry(blog);
    entry.setTitle("My Title");
    entry.setAuthor("author");
    entry.setBody("body");
    entry.setDate(new Date(1000));

    Category category = new Category("categoryId", "category");
    category.setBlog(blog);
    entry.setCategories(Collections.singleton(category));

    model.put(Constants.BLOG_KEY, blog);
    model.put(Constants.BLOG_ENTRIES, Collections.singleton(entry));

    SyndFeed feed = feedView.getFeed();
    MockHttpServletResponse response = new MockHttpServletResponse();
    response.setWriter(new PrintWriter(System.out));
    feedView.dispatch(new MockHttpServletRequest(), response, null);
    assertEquals("tag:www.yourdomain.com,0000-00-00:default", feed.getUri());
    assertEquals("My blog", feed.getTitle());
    assertEquals(1, feed.getEntries().size());
    SyndEntry feedEntry = (SyndEntry) feed.getEntries().get(0);
    assertEquals("tag:www.yourdomain.com,1970-01-01:default/1000", feedEntry.getUri());
    assertEquals(new Date(1000), feedEntry.getPublishedDate());
    assertEquals("body", ((SyndContent)feedEntry.getContents().get(0)).getValue());
    assertEquals("My Title", feedEntry.getTitle());
    assertEquals(entry.getPermalink(), feedEntry.getLink());
    assertEquals(1, feedEntry.getCategories().size());
    SyndCategory syndCategory = (SyndCategory) feedEntry.getCategories().get(0);
    assertEquals("category", syndCategory.getName());
    assertEquals(category.getPermalink(), syndCategory.getTaxonomyUri());
  }
}
