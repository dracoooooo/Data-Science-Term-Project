package com.ocpsoft.socialpm.gwt.client.local.view.component;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class Span extends HTMLPanel
{
   public Span()
   {
      super("span", "");
   }

   public Span(String s)
   {
      super("span", s);
   }

   public Span(Widget w)
   {
      this();
      super.add(w);
   }

   @Override
   public void add(Widget w)
   {
      super.add(w, getElement());
   }

   public void setInnerHTML(String html)
   {
      getElement().setInnerHTML(html);
   }

   public void setInnerText(String text)
   {
      getElement().setInnerText(text);
   }
}
