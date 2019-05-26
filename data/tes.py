'''
Data structure definition
'''

from nltk import word_tokenize
import xml.etree.ElementTree as ET
from collections import Counter


class Review:
    '''
    define the data structure of a review
    '''
    def __init__(self, id):
        self.id = id
        self.sentences = []


class Sentence:
    '''
    define the data structure of a sentence
    '''
    def __init__(self, id):
        self.id = id
        self.raw_text = ""
        self.words = []
        self.opinions = []
        self.clauses = []


    def __iter__(self):
        for w in self.words:
            yield w

    def __len__(self):
        return len(self.words)

    def __getitem__(self, i):
        return self.words[i]


class Opinion:
    '''
    define the data structure of an opinion
    '''
    def __init__(self, target='', category='', polarity=0, _from=0, to=0):
        pass
        # target - str
        # category - str
        # polarity - +1, 0, -1
        # _from - int
        # to - int
        self.target = target
        self.category = category
        self.polarity = polarity
        self._from = _from
        self.to = to


def pola_atoi(polarity):
    if polarity == 'positive':
        return +1
    if polarity == 'negative':
        return -1
    if polarity == 'neutral':
        return 0


def load_dataset(filename):
    tree = ET.parse(filename)
    root = tree.getroot()
    reviews = []
    for review_node in root.iter('Review'):
        review = Review(review_node.get('rid'))
        for sentence_node in review_node.iter('sentence'):
            sentence = Sentence(sentence_node.get('id'))
            raw_text = sentence_node.find('text').text
            words = word_tokenize(raw_text)
            words = [w.lower() for w in words]
            sentence.words = words
            sentence.raw_text = raw_text

            # clauses
            for clause_node in sentence_node.iter('clause'):
                #sentence.clauses.append(clause_node.text)
                if clause_node.text is None:
                    continue
                clause = Sentence(id="-1")
                clause_alltext = clause_node.text
                if clause_alltext.find("$@")>0:
                    catstring = clause_alltext[clause_alltext.index("$@")+2:]
                    clause.raw_text = clause_alltext[:clause_alltext.index("$@")]
                    categ = catstring.split("@")
                    for c in categ:
                        opinion = Opinion()
                        opinion.category = c
                        clause.opinions.append(opinion)
                else :
                    clause.raw_text = clause_alltext

                clause.words = word_tokenize(clause.raw_text)
                clause.words = [w.lower() for w in clause.words]
                sentence.clauses.append(clause)

            # opinions
            for opi in sentence_node.iter('Opinion'):
                opinion = Opinion()
                opinion.target = opi.get('target')
                opinion.category = opi.get('category')
                opinion.polarity = pola_atoi(opi.get('polarity'))
                if opinion.target:
                    opinion._from = int(opi.get('from'))
                    opinion.to = int(opi.get('to'))

                sentence.opinions.append(opinion)
            review.sentences.append(sentence)

        reviews.append(review)

    return reviews

def unwrap(reviews):
    '''
    return a list of sentences
    '''
    sentences = []
    for rv in reviews:
        sentences += rv.sentences
    return sentences

def main():
    TRAIN_FILE = "test.xml"
    training_reviews = load_dataset(TRAIN_FILE)
    training_sentences = unwrap(training_reviews)
    print "load data"
    for sentence in training_sentences:
        for clause in sentence.clauses:
            print (clause.raw_text)
            for opinion in clause.opinions:
                print (opinion.category)

if __name__ == '__main__':
    main()
