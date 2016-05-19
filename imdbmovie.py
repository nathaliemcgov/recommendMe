from bs4 import BeautifulSoup
import urllib
import csv
import re

artistList = []

pagesToVisit = range(1, 11)
for eachpage in pagesToVisit:
    url = 'movies/' + str(eachpage) + '.html'
    page = urllib.urlopen(url)
    soup = BeautifulSoup(page.read())

    artistName = soup.findAll(href=re.compile('title'))
    for artist in artistName:
        artistList.append([unicode(artist.text).encode("utf-8")])

musicFile = open('imdb1000movies.csv', 'w')
writer = csv.writer(musicFile)

print len(artistList)
writer.writerows(artistList)