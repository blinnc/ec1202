local physics = require( "physics" )
physics.start()
physics.setGravity( 0, 98)

--draw the characters
local cityBack = display.newImage( "cityScape.jpg")
local cityBack2 = display.newImage( "cityScape.jpg")
local cityBack3 = display.newImage( "cityScape.jpg")
local character = display.newImage( "panda.png", 170, 200)
local ground = display.newImage( "ground.png" )
local ground2 = display.newImage( "ground.png" )
local ground3 = display.newImage( "ground.png" )
local s = 0
local jumpCount = 0
local score = display.newText("Score: " .. s, display.contentWidth - 70, 0, native.systemFont, 20)
score:setTextColor(0, 0, 255)
ground.x = 0
ground.y = 320
ground2.y = ground.y
ground2.x = ground.x + ground.contentWidth
ground3.y = ground2.y
ground3.x = ground2.x + ground.contentWidth
ground.myName = ground
cityBack2.x = cityBack.x + cityBack.contentWidth
cityBack3.x = cityBack.x - cityBack.contentWidth

physics.addBody( ground, "static", { friction=0.5, bounce=0.0 } )
physics.addBody( ground2, "static", { friction=0.5, bounce=0.0 } )
physics.addBody( ground3, "static", { friction=0.5, bounce=0.0 } )
physics.addBody( character, { density=0, friction=0.5, bounce=0.0 } )
character.isFixedRotation = true
 
--variables: booleans for tracking jumping and timer for moving the backgrounds
local tPrevious = system.getTimer()
local jumping = false
local slashing = false
local up = false
local down = false
--initial vertical speed for jumping
local yInitial = 40
local yChange = 4

numCrates = 0

local t = {}
crates = {}

--redraw function that constantly updates all of the graphics
local function redraw(event)

	--get the distance by which we are going to translate the backgrounds
	s = s + 5
	score.text = "Score: " .. s
	
	if character.x < -character.contentWidth * 3 or character.y > display.contentHeight * 3 then
		character.x = 170
		character.y = -10
		s = 0
		character:setLinearVelocity( 0, 0 )
	end
	
	local tDelta = event.time - tPrevious
	tPrevious = event.time
	local xOffset = ( 1 * tDelta )
	
	--translate the background
	cityBack.x = cityBack.x - xOffset * 0.1
	cityBack2.x = cityBack2.x - xOffset * 0.1
	cityBack3.x = cityBack3.x - xOffset * 0.1
	
	ground.x = ground.x - xOffset * .1
	ground2.x = ground2.x - xOffset * .1
	ground3.x = ground3.x - xOffset * .1
	
	--if the background is too far over...
	if cityBack.x < -cityBack.contentWidth then
		--move the background back to where it was
		cityBack.x = cityBack3.x + cityBack.contentWidth
	end
	
	if cityBack2.x < -cityBack.contentWidth then
		--move the background back to where it was
		cityBack2.x = cityBack.x + cityBack.contentWidth
	end
	
	if cityBack3.x < -cityBack.contentWidth then
		--move the background back to where it was
		cityBack3.x = cityBack2.x + cityBack.contentWidth
	end
	
	if ground.x < -2 * ground.contentWidth/3 then
		ground.x = ground3.x + ground.contentWidth + math.random(50) + 75
		ground.y = 340 - math.random(55)
	end
	
	if ground2.x < -2 * ground.contentWidth/3 then
		ground2.x = ground.x + ground.contentWidth + math.random(50) + 75
		ground2.y = 340 - math.random(55)
	end
	
	if ground3.x < -2 * ground.contentWidth/3 then
		ground3.x = ground2.x + ground.contentWidth + math.random(50) + 75
		ground3.y = 340 - math.random(55)
	end
	
	--crate.x = crate.x - 5
	--crate.x = crate.x - xOffset*0.1
	
	--if a jump has been detected
	
	for index,crate in ipairs(crates) do
		crate.x = crate.x - xOffset*0.1
	end
end

local function spawnCrate()
	numCrates = numCrates + 1
	crate = display.newImage( "crate.png", math.random(400) + 600, -50)
	crate.rotation = 10
	physics.addBody( crate, { density=1.0, friction=10.0, bounce = .01 } )
	crate:setLinearVelocity( -100, 0)
	table.insert(crates, crate)
end

local function removeCrate()
	for index,crate in ipairs(crates) do
		if crate.x < -100  then
			table.remove(crates, index)
		end
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
			--print("jumping")
			--jumping = true
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

local function tap(event)
	print("tapped! | " .. jumpCount)
	local vx, vy
	vx, vy = character:getLinearVelocity()
	print(vy)
	if vy > -30 and vy < 30 then
		jumpCount = 0
	end
	if jumpCount < 2 then
		character:setLinearVelocity(0 , -800)
		jumpCount = jumpCount + 1
	end
	return true
end

timer.performWithDelay ( 1000, spawnCrate, 100 )
timer.performWithDelay ( 5000, removeCrate, 100 )

Runtime:addEventListener("enterFrame",redraw)
-- look into the "tap" event for jumping
Runtime:addEventListener("tap", tap)
Runtime:addEventListener("touch",onTouch)