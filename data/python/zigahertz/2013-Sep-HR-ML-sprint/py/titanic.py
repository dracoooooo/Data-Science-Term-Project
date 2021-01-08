import os
import csv as csv
import numpy as np
import matplotlib.pyplot as plt

path = os.getcwd()

csv_file_object = csv.reader(open(path + '/train.csv', 'rb'))

header = csv_file_object.next()

data = []
for row in csv_file_object:
    data.append(row)
data = np.array(data)

number_passengers = np.size(data[0::, 0].astype(np.float))
number_survived = np.sum(data[0::, 0].astype(np.float))
prop_surv = number_survived / number_passengers  # proportion of survivors

print '# passengers: ' + str(number_passengers)
print '# survived: ' + str(number_survived)

print '# % survive: ' + str(prop_surv)
print

women_only_stats = data[0::, 3] == 'female'
men_only_stats = data[0::, 3] != 'female'
women_onboard = data[women_only_stats, 0].astype(np.float)
men_onboard = data[men_only_stats, 0].astype(np.float)

print 'wos: ' + str(len(women_only_stats))

proportion_women_survived = np.sum(women_onboard) / np.size(women_onboard)
proportion_men_survived = np.sum(men_onboard) / np.size(men_onboard)

print 'proportion women survived: %s' % proportion_women_survived
print 'proportion men survived: %s' % proportion_men_survived

test_file_object = csv.reader(open(path + '/test.csv', 'rb'))
header = test_file_object.next()
open_file_object = csv.writer(open(path + '/genderbasedmodelpy.csv', 'wb'))

for row in test_file_object:
  if row[2] == 'female':
    row.insert(0, '1')
    open_file_object.writerow(row)
  else:
    row.insert(0, '0')
    open_file_object.writerow(row)

### Multivariate Prediction ###

fare_ceiling = 40
data[data[0::,8].astype(np.float) >= fare_ceiling, 8] = fare_ceiling - 1.0
fare_bracket_size = 10
num_price_brackets = fare_ceiling / fare_bracket_size
num_classes = 3

survival_table = np.zeros((2, num_classes, num_price_brackets))

for i in xrange(num_classes):
  for j in xrange(num_price_brackets):

    women_only_stats = data[(data[0::, 3] == 'female') & \
      (data[0::, 1].astype(np.float) == i+1) & \
      (data[0:, 8].astype(np.float) >= j * fare_bracket_size) & \
      (data[0:, 8].astype(np.float) < (j+1) * fare_bracket_size), 0]

    men_only_stats = data[(data[0::, 3] != 'female') & \
      (data[0::, 1].astype(np.float) == i+1) & \
      (data[0:, 8].astype(np.float) >= j * fare_bracket_size) & \
      (data[0:, 8].astype(np.float) < (j+1) * fare_bracket_size), 0]

    survival_table[0, i, j] = np.mean(women_only_stats.astype(np.float))
    survival_table[1, i, j] = np.mean(men_only_stats.astype(np.float))

survival_table[survival_table != survival_table] = 0
survival_table[survival_table < 0.5] = 0
survival_table[survival_table >= 0.5] = 1

print survival_table

test_file_object = csv.reader(open(path + '/test.csv', 'rb'))
new_open_file_object = csv.writer(open(path + '/genderclasspricebasedmodelpy.csv', 'wb'))
header = test_file_object.next()

for row in test_file_object:
  for j in xrange(num_price_brackets):
    try:
      row[7] = float(row(7))
    except:
      bin_fare = 3 - float(row[0])
      break
    if row[7] > fare_ceiling:
      bin_fare = num_price_brackets - 1
      break
    if row[7] >= j*fare_bracket_size and row[7] < (j+1)*fare_bracket_size:
      bin_fare = j
      break

  if row[2] == 'female':
    row.insert(0, int(survival_table[0, float(row[0])-1, bin_fare]))
    new_open_file_object.writerow(row)
  else:
    row.insert(0, int(survival_table[1, float(row[0])-1, bin_fare]))
    new_open_file_object.writerow(row)



