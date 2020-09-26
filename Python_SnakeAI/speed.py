import time

start = time.time()

for _ in range(0, 10):
	for _ in range(0, 1000):
		for n in range(0, 1000):
			x = n*0.1
			x += 1

print("Det tok {} sekunder".format(time.time() - start))