def printbestprediction():
    lines_base = open( "2015_Laptop_lstm.txt" ).readlines()
    lines_punc = open( "2015_Laptop_punc_lstm.txt" ).readlines()
    lines_tree = open( "2015_Laptop_tree_lstm.txt" ).readlines()
    outlines = ""
    for i in xrange( 0, len( lines_base ), 3 ):
        outlines += lines_base[i]
        # outlines += '\n'
        start = lines_base[i + 1].index("[")
        gold_s = lines_base[i + 1][start+1:-3]
        outlines += gold_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #baseline
        start = lines_base[i + 2].index( "[" )
        base_s = lines_base[i + 2][start+1:-3]
        outlines += base_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #punc
        start = lines_punc[i + 2].index( "[" )
        punc_s = lines_punc[i + 2][start + 1:-3]
        outlines += punc_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #tree
        start = lines_tree[i + 2].index( "[" )
        tree_s = lines_tree[i + 2][start + 1:-3]
        outlines += tree_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'
    output = open("2015_laptop_all.txt", 'w')
    output.write(outlines)

def printbestprediction_r():
    lines_base = open( "2015_restaurant_lstm.txt" ).readlines()
    lines_punc = open( "2015_restaurant_punc_lstm.txt" ).readlines()
    lines_tree = open( "2015_restaurant_tree_lstm.txt" ).readlines()
    outlines = ""
    for i in xrange( 0, len( lines_base ), 3 ):
        outlines += lines_base[i]
        # outlines += '\n'
        start = lines_base[i + 1].index("[")
        gold_s = lines_base[i + 1][start+1:-3]
        outlines += gold_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #baseline
        start = lines_base[i + 2].index( "[" )
        base_s = lines_base[i + 2][start+1:-3]
        outlines += base_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #punc
        start = lines_punc[i + 2].index( "[" )
        punc_s = lines_punc[i + 2][start + 1:-3]
        outlines += punc_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'

        #tree
        start = lines_tree[i + 2].index( "[" )
        tree_s = lines_tree[i + 2][start + 1:-3]
        outlines += tree_s.replace(',', '').replace('\'', '').strip('\n')
        outlines += '\n'
    output = open("2015_restaurant_all.txt", 'w')
    output.write(outlines)

printbestprediction()
printbestprediction_r()