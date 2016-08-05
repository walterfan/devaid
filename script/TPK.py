nums = Array.new
puts "Enter a number at each prompt"
for i in 0...11
  print ">"
  nums[i]= gets.to_i
end
nums.reverse.each do |x|
  y = Math.sqrt(x.abs) + 5*x**3
  puts " for x=#{x} result is #{(y>400) ? ' too large' : y}"
end