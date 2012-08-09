require 'ruboto/activity'
require 'ruboto/widget'
require 'ruboto/util/toast'

ruboto_import_widgets :Button, :LinearLayout, :TextView

# http://xkcd.com/378/

class MultiactActivity
  def on_create(bundle)
    setTitle "main activity"
    self.content_view = 
      linear_layout(:orientation => :vertical) do
        @text_view = text_view :text => 'What hath Matz wrought?', :id => 42, :width => :fill_parent,
                             :gravity => android.view.Gravity::CENTER, :text_size => 48.0
        button :text => "Next activity", :id => 43, :width => :fill_parent, :on_click_listener => proc { on_click_next }
      end
  end
  def on_click_next
    start_ruboto_activity("$activity_next") do
      def on_create(bundle)
        self.content_view =
          linear_layout(:orientation => :vertical) do
            @text_view = text_view :text => 'What is next?', :id => 44, :width => :fill_parent,
                                 :gravity => android.view.Gravity::CENTER, :text_size => 48.0
          end
      end
      nil
    end

  end
end
