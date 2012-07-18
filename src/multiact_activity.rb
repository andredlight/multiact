require 'ruboto/activity'
require 'ruboto/widget'
require 'ruboto/util/toast'

ruboto_import_widgets :Button, :LinearLayout, :TextView

# http://xkcd.com/378/

class RubotoActivity
  def self.main(context)
    context.start_ruboto_activity("$activity_main") do
      def on_create(bundle)
        setTitle "main activity"
        setContentView(
          linear_layout(:orientation => :vertical) do
            @text_view = text_view :text => 'What hath Matz wrought?', :id => 42, :width => :fill_parent,
                                 :gravity => android.view.Gravity::CENTER, :text_size => 48.0
            button :text => "Next activity", :id => 43, :width => :fill_parent, :on_click_listener => @on_click_next
          end
        )
      end
      @on_click_next = proc do |button|
        RubotoActivity.next(self)
      end
    end
  end

  def self.next(context)
    context.start_ruboto_activity("$activity_next") do
      def on_create(bundle)
        setContentView(
          linear_layout(:orientation => :vertical) do
            @text_view = text_view :text => 'What is next?', :id => 44, :width => :fill_parent,
                                 :gravity => android.view.Gravity::CENTER, :text_size => 48.0
          end
        )
      end
    end
  end
end

RubotoActivity.main $activity 
