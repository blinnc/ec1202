local cityBack = display.newImage( "cityScape.jpg", 0, 0)
local character = display.newImage( "panda.jpg", 170, 250)

local tPrevious = system.getTimer()
local jumping = false
local slashing = false
local up = false
local down = false

local function move(event)
	local tDelta = event.time - tPrevious
	tPrevious = event.time

	local xOffset = ( 0.2 * tDelta )
	
	cityBack.x = cityBack.x - xOffset*0.1
	
	if cityBack.x < 0 then
		cityBack:translate ( 240 , 0)
	end
	if jumping == true then
		if down == false then
			character.y = character.y - 10
			if character.y < 130 then
				up = false
				down = true
			end
		end
		if down == true then
			character.y = character.y + 10
			if character.y > 250 then
				jumping = false
				down = false
			end
		end
	end
end

local function onTouch(event)
	local phase = event.phase
	if phase == "ended" then
		if slashing == false then
			print("jumping")
			jumping = true
		else
			slashing = false
		end
	end
	if phase == "moved" then
		slashing = true
		print("slashing")
	end
	return true
end

Runtime:addEventListener("enterFrame",move)
Runtime:addEventListener("touch",onTouch)