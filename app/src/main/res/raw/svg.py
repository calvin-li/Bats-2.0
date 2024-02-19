from os import system
svg = r'<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="50px" height="50px" viewBox="-50 -80 100 100" xml:space="preserve">   <text      x="0" y="0" fill="white" stroke="COLOR" stroke-width="5px" font-size="85px" text-anchor="middle" font-weight="bold" font-family="monospace" transform="scale(XSCALE,1)">PERCENT</text></svg>'
for i in range(0, 101):
	color = "black"
	if i < 20:
		color = "red"
	elif i < 40:
		color = "orange"
	elif i < 60:
		color = "yellow"
	elif i < 80:
		color = "yellowgreen"
	else:
		color = "forestgreen"
	xscale = "1"
	if i == 100:
		xscale = "0.6"
	out = svg.replace("COLOR", color)
	out = out.replace("PERCENT",str(i))
	out = out.replace("XSCALE", xscale)
	file = "percent/" + str(i) + ".svg"
	with open(file, "w") as f:
		f.write(out)
	f.closed
	png = "../mipmap-hdpi/a" + str(i) + ".png"
	system("inkscape -f " + file + " -e " + png)