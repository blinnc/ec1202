local physics = require( "physics" )
physics.start()

--draw the characters
local cityBack = display.newImage( "cityScape.jpg", 0, 0)
local character = display.newImage( "panda.jpg", 170, 50)
local crate = display.newImage( "smallcrate.jpg", 250, 100)
local ground = display.newImage( "ground.png" )
ground.x = display.contentWidth / 2
ground.y = 320
ground.myName = ground

physics.addBody( ground, "static", { friction=0.5, bounce=0.0 } )
physics.addBody( crate, { density=10.0, friction=10.0, bounce=0.0 } )
physics.addBody( character, { density=10.0, friction=10.0, bounce=0.0 } )

--variables: booleans for tracking jumping and timer for moving the backgrounds
local tPrevious = system.getTimer()
local jumping = false
local slashing = false
local up = false
local down = false
--initial vertical speed for jumping
local yInitial = 30
local yChange = 4

--redraw function that constantly updates all of the graphics
local function redraw(event)

	--get the distance by which we are going to translate the backgrounds
	local tDelta = event.time - tPrevious
	tPrevious = event.time
	local xOffset = ( 0.2 * tDelta )
	
	--translate the background
	cityBack.x = cityBack.x - xOffset*0.1
	
	--if the background is too far over...
	if cityBack.x < 0 then
		--move the background back to where it was
		cityBack:translate ( 240 , 0)
	end
	
	--crate.x = crate.x - 5
	
	--if a jump has been detected
	if jumping == true then
		
		if down == false then
			--character.y = character.y - 10
			yInitial = yInitial - yChange
			character.y = character.y - yInitial
			--if character.y < 130 then
				--up = false
				--down = true
			--end
			if character.y >= 250 then
				yInitial = 30
				jumping = false
				character.y = 250
			end
		end
		--translate down when he is jumping down
		--if down == true then
			--character.y = character.y + 10
			--yInitial = yInitial + 1
			--character.y = character.y + yInitial
			--if character.y > 250 then
				--jumping = false
				--down = false
				--yInitial = 15
			--end
		--end
	end
end

local function onTouch(event)
	--get the phase of the event
	local phase = event.phase
	--when the event ends...
	if phase == "ended" then
		--if we never saw a slash...
		if slashing == false then
			--we jump!
			print("jumping")
			jumping = true
		else
			slashing = false
		end
	end
	--if we detect a drag
	if phase == "moved" then
		--slashy slashy
		slashing = true
		print("slashing")
	end
	return true
end

Runtime:addEventListener("enterFrame",redraw)
Runtime:addEventListener("touch",onTouch)