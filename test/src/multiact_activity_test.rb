activity Java::org.rubototest.multiact.MultiactActivity

def wait_for_activity(activity, id, timeout=60)
  start = Time.now
  loop do
    @thing = activity.findViewById(39)
    break if @thing || (Time.now - start > timeout)
    sleep 1
  end
  assert @thing
end

test('initial setup') do |activity|
  assert_equal "What hath Matz wrought?", @thing.text
end

test('next activity works') do |activity|
  button = activity.findViewById(41)
  button.performClick
  wait_for_activity(activity, 44)
  assert_equal "What is next?", @thing.text
end
