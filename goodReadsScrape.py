from bs4 import BeautifulSoup
import urllib
import csv

bookList = []

pagesToVisit = range(1, 6)
for eachPage in pagesToVisit:
    url = 'http://www.goodreads.com/list/show/6675.The_Guardian_s_1000_Novels_Everyone_Must_Read_?page=%s' % (eachPage)
    page = urllib.urlopen(url)
    soup = BeautifulSoup(page.read())

    bookTitle = soup.find_all('span', {'itemprop' : 'name'})
    for title in bookTitle:
        bookList.append([unicode(title.text).encode("utf-8")])

bookFile = open('goodReadsTop1000.csv', 'w')
writer = csv.writer(bookFile)

print len(bookList)
writer.writerows(bookList)