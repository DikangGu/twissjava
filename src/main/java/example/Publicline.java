package example;

import example.models.Timeline;
import example.models.Tweet;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the default home page when not logged in.
 *  It contains the 40 most recent global tweets.
 */
public class Publicline extends HomePage {
    private String username;
    private Long nextpage;

    public Publicline(final PageParameters parameters) {
        super(parameters);
        nextpage = parameters.get("nextpage").toLong(0);
        username = parameters.get("username").toString();
        if (username == null) {
            username = "!PUBLIC!";
            add(new Label("h2name", "Public"));
        }
        else {
            add(new Label("h2name", username + "'s"));
        }

        Timeline timeline = getUserline(username, nextpage);

        List<Tweet> tweets;
        if (timeline == null) {
            tweets = new ArrayList<Tweet>(0);
        } else {
            tweets = timeline.getView();
        }

        if (tweets.size() > 0) {
            add(new ListView<Tweet>("tweetlist", tweets) {
                @Override
                public void populateItem(final ListItem<Tweet> listitem) {
                    listitem.add(new Link("link") {
                        @Override
                        public void onClick() {
                            PageParameters p = new PageParameters();
                            p.add("username", listitem.getModel().getObject().getUname());
                            setResponsePage(Publicline.class, p);
                        }
                    }.add(new Label("tuname",listitem.getModel().getObject().getUname())));
                    listitem.add(new Label("tbody", ": " + listitem.getModel().getObject().getBody()));
                }
            }).setVersioned(false);
            Long linktopaginate = timeline.getNextview();
            if (linktopaginate != null) {
                nextpage = linktopaginate;
                WebMarkupContainer pagediv = new WebMarkupContainer("pagedown");
                PageForm pager = new PageForm("pageform");
                pagediv.add(pager);
                add(pagediv);
            }
            else {
                add(new WebMarkupContainer("pagedown") {
                    @Override
                    public boolean isVisible() {
                        return false;
                    }
                });
            }
        }
        else {
            ArrayList<String> hack = new ArrayList<String>(1);
            hack.add("There are no tweets yet. Log in and post one!");
            add(new ListView<String>("tweetlist", hack ) {
                @Override
                public void populateItem(final ListItem<String> listitem) {
                    listitem.add(new Link("link") {
                        @Override
                        public void onClick() {
                        }
                    }.add(new Label("tuname","")));
                    listitem.add(new Label("tbody", listitem.getModel().getObject()));
                }
            }).setVersioned(false);
            add(new WebMarkupContainer("pagedown") {
                @Override
                public boolean isVisible() {
                    return false;
                }
            });
        }
    }

    private class PageForm extends Form {
        public PageForm(String id) {
            super(id);
        }
        @Override
        public void onSubmit() {
            PageParameters p = new PageParameters();
            p.add("nextpage", nextpage);
            setResponsePage(getPage().getClass(), p);
        }
    }
}
