require 'ruboto/activity'
require 'ruboto/widget'
require 'ruboto/util/toast'

ruboto_import_widgets :Button, :LinearLayout, :TextView

# http://xkcd.com/378/

class MultiactActivity
  def on_create(bundle)
    setTitle "main activity"
    setContentView(
        linear_layout(:orientation => :vertical) do
          @text_view = text_view :text => 'What hath Matz wrought?', :id => 42, :width => :fill_parent,
                                 :gravity => android.view.Gravity::CENTER, :text_size => 48.0
          button :text => "Next activity", :id => 43, :width => :fill_parent, :on_click_listener => proc { next_activity ; nil }
        end
    )
  end

  private

  def next_activity
    puts 'Start next activity'
    start_ruboto_activity("$activity_next") do
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
