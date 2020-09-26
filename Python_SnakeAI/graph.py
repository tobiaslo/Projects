
import matplotlib.pyplot as plt

fil = open("graph.txt")
data = []

for linje in fil:
	data.append(linje.strip().split(","))

print(data)
for n in range(0,len(data)):
	for i in range(0, len(data[n])):
		print(data[n][i])
		data[n][i] = int(float(data[n][i]))

for f in data:
	plt.plot(f)

plt.show()



