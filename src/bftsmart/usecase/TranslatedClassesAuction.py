n = 3
principals = [4, 7, 1]
oC = [ True, True, True ]
oCI = [ [ Int("oCI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
oCIrange0 = [ And(0 <= oCI[i][j]) for i in range(n) for j in range(n) ]
s.add(oCIrange0)
oCIrange1 = [And(sLe(oCI[i], principals)) for i in range(n)]
s.add(oCIrange1)
oCA = [ [ Int("oCA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
oCArange0 = [ And(0 <= oCA[i][j]) for i in range(n) for j in range(n) ]
s.add(oCArange0)
oCArange1 = [And(sLe(oCA[i], principals)) for i in range(n)]
s.add(oCArange1)
startC = [ True, True, True ]
startI = [[ 4, 7, 0], [ 0, 0, 0], [ 0, 0, 0] ]
startA = [[ 4, 7, 0], [ 0, 0, 0], [ 0, 0, 0] ]
botC = [ True, True, True ]
botI = [[ 4, 7, 0], [ 0, 0, 0], [ 0, 0, 0] ]
botA = [[ 4, 7, 0], [ 0, 0, 0], [ 0, 0, 0] ]
resultC = [ False, False, True ]
resultI = [[ 1, 2, 0], [ 0, 0, 0], [ 0, 0, 0] ]
resultA = [[ 1, 2, 0], [ 0, 0, 0], [ 0, 0, 0] ]
resH = [0, 0, 1]
resQ = [ [ Int("resQ_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
s.add([ And(0 <= resQ[i][j]) for i in range(n) for j in range(n) ])
s.add([ And(sLe(resQ[i], principals)) for i in range(n) ])
m0H = [ Int('m0H_%s' % i) for i in range(n) ] 
m0Q = [ [ Int('m0Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m0conxtC = [ Bool('m0conxtC_%s' % i) for i in range(n) ]
m0conxtI = [ [ Int('m0conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m0conxtA = [ [ Int('m0conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m0oC = [ Bool('m0oC_%s' % i) for i in range(n) ]
m0oI = [ [ Int('m0oI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m0oA = [ [ Int('m0oA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m0range0 = [ And(0 <= m0conxtI[i][j], 0 <= m0conxtA[i][j], 0 <= m0Q[i][j], 0 <= m0oI[i][j], 0 <= m0oA[i][j]) for i in range(n) for j in range(n) ]
s.add(m0range0)
m0range1 = [And(sLe(m0conxtI[i], principals), sLe(m0conxtA[i], principals), sLe(m0Q[i], principals), sLe(m0oI[i], principals), sLe(m0oA[i], principals)) for i in range(n)]
s.add(m0range1)
m0range2 = [And(0 <= m0H[i]) for i in range(n)]
s.add(m0range2)
s.add(sLe(m0H, principals))
s.add(Not(nonCheck(m0H)))
s.add(Not(nonCheckQ(m0Q)))
m1H = [ Int('m1H_%s' % i) for i in range(n) ] 
m1Q = [ [ Int('m1Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m1conxtC = [ Bool('m1conxtC_%s' % i) for i in range(n) ]
m1conxtI = [ [ Int('m1conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m1conxtA = [ [ Int('m1conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m1offerAC = [ Bool('m1offerAC_%s' % i) for i in range(n) ]
m1offerAI = [ [ Int('m1offerAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m1offerAA = [ [ Int('m1offerAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m1range0 = [ And(0 <= m1conxtI[i][j], 0 <= m1conxtA[i][j], 0 <= m1Q[i][j], 0 <= m1offerAI[i][j], 0 <= m1offerAA[i][j]) for i in range(n) for j in range(n) ]
s.add(m1range0)
m1range1 = [And(sLe(m1conxtI[i], principals), sLe(m1conxtA[i], principals), sLe(m1Q[i], principals), sLe(m1offerAI[i], principals), sLe(m1offerAA[i], principals)) for i in range(n)]
s.add(m1range1)
m1range2 = [And(0 <= m1H[i]) for i in range(n)]
s.add(m1range2)
s.add(sLe(m1H, principals))
s.add(Not(nonCheck(m1H)))
s.add(Not(nonCheckQ(m1Q)))
m2H = [ Int('m2H_%s' % i) for i in range(n) ] 
m2Q = [ [ Int('m2Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2conxtC = [ Bool('m2conxtC_%s' % i) for i in range(n) ]
m2conxtI = [ [ Int('m2conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2conxtA = [ [ Int('m2conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2offerBC = [ Bool('m2offerBC_%s' % i) for i in range(n) ]
m2offerBI = [ [ Int('m2offerBI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2offerBA = [ [ Int('m2offerBA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2offerAC = [ Bool('m2offerAC_%s' % i) for i in range(n) ]
m2offerAI = [ [ Int('m2offerAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2offerAA = [ [ Int('m2offerAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2seatInfoBC = [ Bool('m2seatInfoBC_%s' % i) for i in range(n) ]
m2seatInfoBI = [ [ Int('m2seatInfoBI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2seatInfoBA = [ [ Int('m2seatInfoBA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m2range0 = [ And(0 <= m2conxtI[i][j], 0 <= m2conxtA[i][j], 0 <= m2Q[i][j], 0 <= m2offerBI[i][j], 0 <= m2offerBA[i][j], 0 <= m2offerAI[i][j], 0 <= m2offerAA[i][j], 0 <= m2seatInfoBI[i][j], 0 <= m2seatInfoBA[i][j]) for i in range(n) for j in range(n) ]
s.add(m2range0)
m2range1 = [And(sLe(m2conxtI[i], principals), sLe(m2conxtA[i], principals), sLe(m2Q[i], principals), sLe(m2offerBI[i], principals), sLe(m2offerBA[i], principals), sLe(m2offerAI[i], principals), sLe(m2offerAA[i], principals), sLe(m2seatInfoBI[i], principals), sLe(m2seatInfoBA[i], principals)) for i in range(n)]
s.add(m2range1)
m2range2 = [And(0 <= m2H[i]) for i in range(n)]
s.add(m2range2)
s.add(sLe(m2H, principals))
s.add(Not(nonCheck(m2H)))
s.add(Not(nonCheckQ(m2Q)))
m3H = [ Int('m3H_%s' % i) for i in range(n) ] 
m3Q = [ [ Int('m3Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3conxtC = [ Bool('m3conxtC_%s' % i) for i in range(n) ]
m3conxtI = [ [ Int('m3conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3conxtA = [ [ Int('m3conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3offerAC = [ Bool('m3offerAC_%s' % i) for i in range(n) ]
m3offerAI = [ [ Int('m3offerAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3offerAA = [ [ Int('m3offerAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3seatInfoBC = [ Bool('m3seatInfoBC_%s' % i) for i in range(n) ]
m3seatInfoBI = [ [ Int('m3seatInfoBI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3seatInfoBA = [ [ Int('m3seatInfoBA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3uC = [ Bool('m3uC_%s' % i) for i in range(n) ]
m3uI = [ [ Int('m3uI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3uA = [ [ Int('m3uA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m3range0 = [ And(0 <= m3conxtI[i][j], 0 <= m3conxtA[i][j], 0 <= m3Q[i][j], 0 <= m3offerAI[i][j], 0 <= m3offerAA[i][j], 0 <= m3seatInfoBI[i][j], 0 <= m3seatInfoBA[i][j], 0 <= m3uI[i][j], 0 <= m3uA[i][j]) for i in range(n) for j in range(n) ]
s.add(m3range0)
m3range1 = [And(sLe(m3conxtI[i], principals), sLe(m3conxtA[i], principals), sLe(m3Q[i], principals), sLe(m3offerAI[i], principals), sLe(m3offerAA[i], principals), sLe(m3seatInfoBI[i], principals), sLe(m3seatInfoBA[i], principals), sLe(m3uI[i], principals), sLe(m3uA[i], principals)) for i in range(n)]
s.add(m3range1)
m3range2 = [And(0 <= m3H[i]) for i in range(n)]
s.add(m3range2)
s.add(sLe(m3H, principals))
s.add(Not(nonCheck(m3H)))
s.add(Not(nonCheckQ(m3Q)))
m4H = [ Int('m4H_%s' % i) for i in range(n) ] 
m4Q = [ [ Int('m4Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4conxtC = [ Bool('m4conxtC_%s' % i) for i in range(n) ]
m4conxtI = [ [ Int('m4conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4conxtA = [ [ Int('m4conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4offerAC = [ Bool('m4offerAC_%s' % i) for i in range(n) ]
m4offerAI = [ [ Int('m4offerAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4offerAA = [ [ Int('m4offerAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4uC = [ Bool('m4uC_%s' % i) for i in range(n) ]
m4uI = [ [ Int('m4uI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4uA = [ [ Int('m4uA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m4range0 = [ And(0 <= m4conxtI[i][j], 0 <= m4conxtA[i][j], 0 <= m4Q[i][j], 0 <= m4offerAI[i][j], 0 <= m4offerAA[i][j], 0 <= m4uI[i][j], 0 <= m4uA[i][j]) for i in range(n) for j in range(n) ]
s.add(m4range0)
m4range1 = [And(sLe(m4conxtI[i], principals), sLe(m4conxtA[i], principals), sLe(m4Q[i], principals), sLe(m4offerAI[i], principals), sLe(m4offerAA[i], principals), sLe(m4uI[i], principals), sLe(m4uA[i], principals)) for i in range(n)]
s.add(m4range1)
m4range2 = [And(0 <= m4H[i]) for i in range(n)]
s.add(m4range2)
s.add(sLe(m4H, principals))
s.add(Not(nonCheck(m4H)))
s.add(Not(nonCheckQ(m4Q)))
m5H = [ Int('m5H_%s' % i) for i in range(n) ] 
m5Q = [ [ Int('m5Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5conxtC = [ Bool('m5conxtC_%s' % i) for i in range(n) ]
m5conxtI = [ [ Int('m5conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5conxtA = [ [ Int('m5conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5offerAC = [ Bool('m5offerAC_%s' % i) for i in range(n) ]
m5offerAI = [ [ Int('m5offerAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5offerAA = [ [ Int('m5offerAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5uC = [ Bool('m5uC_%s' % i) for i in range(n) ]
m5uI = [ [ Int('m5uI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5uA = [ [ Int('m5uA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5seatInfoAC = [ Bool('m5seatInfoAC_%s' % i) for i in range(n) ]
m5seatInfoAI = [ [ Int('m5seatInfoAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5seatInfoAA = [ [ Int('m5seatInfoAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5oC = [ Bool('m5oC_%s' % i) for i in range(n) ]
m5oI = [ [ Int('m5oI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5oA = [ [ Int('m5oA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m5range0 = [ And(0 <= m5conxtI[i][j], 0 <= m5conxtA[i][j], 0 <= m5Q[i][j], 0 <= m5offerAI[i][j], 0 <= m5offerAA[i][j], 0 <= m5uI[i][j], 0 <= m5uA[i][j], 0 <= m5seatInfoAI[i][j], 0 <= m5seatInfoAA[i][j], 0 <= m5oI[i][j], 0 <= m5oA[i][j]) for i in range(n) for j in range(n) ]
s.add(m5range0)
m5range1 = [And(sLe(m5conxtI[i], principals), sLe(m5conxtA[i], principals), sLe(m5Q[i], principals), sLe(m5offerAI[i], principals), sLe(m5offerAA[i], principals), sLe(m5uI[i], principals), sLe(m5uA[i], principals), sLe(m5seatInfoAI[i], principals), sLe(m5seatInfoAA[i], principals), sLe(m5oI[i], principals), sLe(m5oA[i], principals)) for i in range(n)]
s.add(m5range1)
m5range2 = [And(0 <= m5H[i]) for i in range(n)]
s.add(m5range2)
s.add(sLe(m5H, principals))
s.add(Not(nonCheck(m5H)))
s.add(Not(nonCheckQ(m5Q)))
m6H = [ Int('m6H_%s' % i) for i in range(n) ] 
m6Q = [ [ Int('m6Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6conxtC = [ Bool('m6conxtC_%s' % i) for i in range(n) ]
m6conxtI = [ [ Int('m6conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6conxtA = [ [ Int('m6conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6uC = [ Bool('m6uC_%s' % i) for i in range(n) ]
m6uI = [ [ Int('m6uI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6uA = [ [ Int('m6uA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6seatInfoAC = [ Bool('m6seatInfoAC_%s' % i) for i in range(n) ]
m6seatInfoAI = [ [ Int('m6seatInfoAI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6seatInfoAA = [ [ Int('m6seatInfoAA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6oC = [ Bool('m6oC_%s' % i) for i in range(n) ]
m6oI = [ [ Int('m6oI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6oA = [ [ Int('m6oA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m6range0 = [ And(0 <= m6conxtI[i][j], 0 <= m6conxtA[i][j], 0 <= m6Q[i][j], 0 <= m6uI[i][j], 0 <= m6uA[i][j], 0 <= m6seatInfoAI[i][j], 0 <= m6seatInfoAA[i][j], 0 <= m6oI[i][j], 0 <= m6oA[i][j]) for i in range(n) for j in range(n) ]
s.add(m6range0)
m6range1 = [And(sLe(m6conxtI[i], principals), sLe(m6conxtA[i], principals), sLe(m6Q[i], principals), sLe(m6uI[i], principals), sLe(m6uA[i], principals), sLe(m6seatInfoAI[i], principals), sLe(m6seatInfoAA[i], principals), sLe(m6oI[i], principals), sLe(m6oA[i], principals)) for i in range(n)]
s.add(m6range1)
m6range2 = [And(0 <= m6H[i]) for i in range(n)]
s.add(m6range2)
s.add(sLe(m6H, principals))
s.add(Not(nonCheck(m6H)))
s.add(Not(nonCheckQ(m6Q)))
m7H = [ Int('m7H_%s' % i) for i in range(n) ] 
m7Q = [ [ Int('m7Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7conxtC = [ Bool('m7conxtC_%s' % i) for i in range(n) ]
m7conxtI = [ [ Int('m7conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7conxtA = [ [ Int('m7conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7uC = [ Bool('m7uC_%s' % i) for i in range(n) ]
m7uI = [ [ Int('m7uI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7uA = [ [ Int('m7uA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7oC = [ Bool('m7oC_%s' % i) for i in range(n) ]
m7oI = [ [ Int('m7oI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7oA = [ [ Int('m7oA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m7range0 = [ And(0 <= m7conxtI[i][j], 0 <= m7conxtA[i][j], 0 <= m7Q[i][j], 0 <= m7uI[i][j], 0 <= m7uA[i][j], 0 <= m7oI[i][j], 0 <= m7oA[i][j]) for i in range(n) for j in range(n) ]
s.add(m7range0)
m7range1 = [And(sLe(m7conxtI[i], principals), sLe(m7conxtA[i], principals), sLe(m7Q[i], principals), sLe(m7uI[i], principals), sLe(m7uA[i], principals), sLe(m7oI[i], principals), sLe(m7oA[i], principals)) for i in range(n)]
s.add(m7range1)
m7range2 = [And(0 <= m7H[i]) for i in range(n)]
s.add(m7range2)
s.add(sLe(m7H, principals))
s.add(Not(nonCheck(m7H)))
s.add(Not(nonCheckQ(m7Q)))
m8H = [ Int('m8H_%s' % i) for i in range(n) ] 
m8Q = [ [ Int('m8Q_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m8conxtC = [ Bool('m8conxtC_%s' % i) for i in range(n) ]
m8conxtI = [ [ Int('m8conxtI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m8conxtA = [ [ Int('m8conxtA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m8oC = [ Bool('m8oC_%s' % i) for i in range(n) ]
m8oI = [ [ Int('m8oI_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m8oA = [ [ Int('m8oA_%s_%s' % (i, j)) for j in range(n) ] for i in range(n) ]
m8range0 = [ And(0 <= m8conxtI[i][j], 0 <= m8conxtA[i][j], 0 <= m8Q[i][j], 0 <= m8oI[i][j], 0 <= m8oA[i][j]) for i in range(n) for j in range(n) ]
s.add(m8range0)
m8range1 = [And(sLe(m8conxtI[i], principals), sLe(m8conxtA[i], principals), sLe(m8Q[i], principals), sLe(m8oI[i], principals), sLe(m8oA[i], principals)) for i in range(n)]
s.add(m8range1)
m8range2 = [And(0 <= m8H[i]) for i in range(n)]
s.add(m8range2)
s.add(sLe(m8H, principals))
s.add(Not(nonCheck(m8H)))
s.add(Not(nonCheckQ(m8Q)))
Aqs = [ [ Int("Aqs_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
Aqc = [ [ Int("Aqc_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AOH = [ Int('AOH_%s' % i) for i in range(n) ] 
AmakeOffer1input0C = [ Bool('AmakeOffer1input0C_%s' % i) for i in range(n) ]
AmakeOffer1input0I = [ [ Int("AmakeOffer1input0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer1input0A = [ [ Int("AmakeOffer1input0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer1input1C = [ Bool('AmakeOffer1input1C_%s' % i) for i in range(n) ]
AmakeOffer1input1I = [ [ Int("AmakeOffer1input1I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer1input1A = [ [ Int("AmakeOffer1input1A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer1outputC = [ True, False, True ]
AmakeOffer1outputI = [ [ Int("AmakeOffer1outputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer1outputA = [ [ Int("AmakeOffer1outputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2input0C = [ Bool('AmakeOffer2input0C_%s' % i) for i in range(n) ]
AmakeOffer2input0I = [ [ Int("AmakeOffer2input0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2input0A = [ [ Int("AmakeOffer2input0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2input1C = [ Bool('AmakeOffer2input1C_%s' % i) for i in range(n) ]
AmakeOffer2input1I = [ [ Int("AmakeOffer2input1I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2input1A = [ [ Int("AmakeOffer2input1A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2outputC = [ True, True, True ]
AmakeOffer2outputI = [ [ Int("AmakeOffer2outputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
AmakeOffer2outputA = [ [ Int("AmakeOffer2outputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
Arange0 = [ And(0 <= Aqs[i][j], 0 <= Aqc[i][j], 0 <= AmakeOffer1outputI[i][j], 0 <= AmakeOffer1outputA[i][j], 0 <= AmakeOffer1input0I[i][j], 0 <= AmakeOffer1input0A[i][j], 0 <= AmakeOffer1input1I[i][j], 0 <= AmakeOffer1input1A[i][j], 0 <= AmakeOffer2outputI[i][j], 0 <= AmakeOffer2outputA[i][j], 0 <= AmakeOffer2input0I[i][j], 0 <= AmakeOffer2input0A[i][j], 0 <= AmakeOffer2input1I[i][j], 0 <= AmakeOffer2input1A[i][j]) for i in range(n) for j in range(n) ]
s.add(Arange0)
Arange1 = [And(sLe(Aqs[i], principals), sLe(Aqc[i], principals), sLe(AmakeOffer1outputI[i], principals), sLe(AmakeOffer1outputA[i], principals), sLe(AmakeOffer1input0I[i], principals), sLe(AmakeOffer1input0A[i], principals), sLe(AmakeOffer1input1I[i], principals), sLe(AmakeOffer1input1A[i], principals), sLe(AmakeOffer2outputI[i], principals), sLe(AmakeOffer2outputA[i], principals), sLe(AmakeOffer2input0I[i], principals), sLe(AmakeOffer2input0A[i], principals), sLe(AmakeOffer2input1I[i], principals), sLe(AmakeOffer2input1A[i], principals)) for i in range(n)]
s.add(Arange1)
Arange2 = [And(0 <= AOH[i]) for i in range(n)]
s.add(Arange2)
Arange3 = sLe(AOH, principals)
s.add(Arange3)
Bqs = [ [ Int("Bqs_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
Bqc = [ [ Int("Bqc_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BOH = [ Int('BOH_%s' % i) for i in range(n) ] 
BmakeOffer1input0C = [ Bool('BmakeOffer1input0C_%s' % i) for i in range(n) ]
BmakeOffer1input0I = [ [ Int("BmakeOffer1input0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer1input0A = [ [ Int("BmakeOffer1input0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer1input1C = [ Bool('BmakeOffer1input1C_%s' % i) for i in range(n) ]
BmakeOffer1input1I = [ [ Int("BmakeOffer1input1I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer1input1A = [ [ Int("BmakeOffer1input1A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer1outputC = [ False, True, True ]
BmakeOffer1outputI = [ [ Int("BmakeOffer1outputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer1outputA = [ [ Int("BmakeOffer1outputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2input0C = [ Bool('BmakeOffer2input0C_%s' % i) for i in range(n) ]
BmakeOffer2input0I = [ [ Int("BmakeOffer2input0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2input0A = [ [ Int("BmakeOffer2input0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2input1C = [ Bool('BmakeOffer2input1C_%s' % i) for i in range(n) ]
BmakeOffer2input1I = [ [ Int("BmakeOffer2input1I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2input1A = [ [ Int("BmakeOffer2input1A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2outputC = [ True, True, True ]
BmakeOffer2outputI = [ [ Int("BmakeOffer2outputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
BmakeOffer2outputA = [ [ Int("BmakeOffer2outputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
Brange0 = [ And(0 <= Bqs[i][j], 0 <= Bqc[i][j], 0 <= BmakeOffer1outputI[i][j], 0 <= BmakeOffer1outputA[i][j], 0 <= BmakeOffer1input0I[i][j], 0 <= BmakeOffer1input0A[i][j], 0 <= BmakeOffer1input1I[i][j], 0 <= BmakeOffer1input1A[i][j], 0 <= BmakeOffer2outputI[i][j], 0 <= BmakeOffer2outputA[i][j], 0 <= BmakeOffer2input0I[i][j], 0 <= BmakeOffer2input0A[i][j], 0 <= BmakeOffer2input1I[i][j], 0 <= BmakeOffer2input1A[i][j]) for i in range(n) for j in range(n) ]
s.add(Brange0)
Brange1 = [And(sLe(Bqs[i], principals), sLe(Bqc[i], principals), sLe(BmakeOffer1outputI[i], principals), sLe(BmakeOffer1outputA[i], principals), sLe(BmakeOffer1input0I[i], principals), sLe(BmakeOffer1input0A[i], principals), sLe(BmakeOffer1input1I[i], principals), sLe(BmakeOffer1input1A[i], principals), sLe(BmakeOffer2outputI[i], principals), sLe(BmakeOffer2outputA[i], principals), sLe(BmakeOffer2input0I[i], principals), sLe(BmakeOffer2input0A[i], principals), sLe(BmakeOffer2input1I[i], principals), sLe(BmakeOffer2input1A[i], principals)) for i in range(n)]
s.add(Brange1)
Brange2 = [And(0 <= BOH[i]) for i in range(n)]
s.add(Brange2)
Brange3 = sLe(BOH, principals)
s.add(Brange3)
userqs = [ [ Int("userqs_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userqc = [ [ Int("userqc_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userOH = [ Int('userOH_%s' % i) for i in range(n) ] 
userdeclareWinnerinput0C = [ Bool('userdeclareWinnerinput0C_%s' % i) for i in range(n) ]
userdeclareWinnerinput0I = [ [ Int("userdeclareWinnerinput0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userdeclareWinnerinput0A = [ [ Int("userdeclareWinnerinput0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userdeclareWinneroutputC = [ False, False, True ]
userdeclareWinneroutputI = [ [ Int("userdeclareWinneroutputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userdeclareWinneroutputA = [ [ Int("userdeclareWinneroutputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userreadbotC = [ Bool('userreadbotC_%s' % i) for i in range(n) ]
userreadbotI = [ [ Int("userreadbotI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userreadbotA = [ [ Int("userreadbotA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userreadoutputC = [ True, True, True ]
userreadoutputI = [ [ Int("userreadoutputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userreadoutputA = [ [ Int("userreadoutputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateinput0C = [ Bool('userupdateinput0C_%s' % i) for i in range(n) ]
userupdateinput0I = [ [ Int("userupdateinput0I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateinput0A = [ [ Int("userupdateinput0A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateinput1C = [ Bool('userupdateinput1C_%s' % i) for i in range(n) ]
userupdateinput1I = [ [ Int("userupdateinput1I_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateinput1A = [ [ Int("userupdateinput1A_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateoutputC = [ Bool('userupdateoutputC_%s' % i) for i in range(n) ]
userupdateoutputI = [ [ Int("userupdateoutputI_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userupdateoutputA = [ [ Int("userupdateoutputA_%s_%s" % (i, j)) for j in range(n) ] for i in range(n) ]
userrange0 = [ And(0 <= userqs[i][j], 0 <= userqc[i][j], 0 <= userdeclareWinneroutputI[i][j], 0 <= userdeclareWinneroutputA[i][j], 0 <= userdeclareWinnerinput0I[i][j], 0 <= userdeclareWinnerinput0A[i][j], 0 <= userreadoutputI[i][j], 0 <= userreadoutputA[i][j], 0 <= userreadbotI[i][j], 0 <= userreadbotA[i][j], 0 <= userupdateoutputI[i][j], 0 <= userupdateoutputA[i][j], 0 <= userupdateinput0I[i][j], 0 <= userupdateinput0A[i][j], 0 <= userupdateinput1I[i][j], 0 <= userupdateinput1A[i][j]) for i in range(n) for j in range(n) ]
s.add(userrange0)
userrange1 = [And(sLe(userqs[i], principals), sLe(userqc[i], principals), sLe(userdeclareWinneroutputI[i], principals), sLe(userdeclareWinneroutputA[i], principals), sLe(userdeclareWinnerinput0I[i], principals), sLe(userdeclareWinnerinput0A[i], principals), sLe(userreadoutputI[i], principals), sLe(userreadoutputA[i], principals), sLe(userreadbotI[i], principals), sLe(userreadbotA[i], principals), sLe(userupdateoutputI[i], principals), sLe(userupdateoutputA[i], principals), sLe(userupdateinput0I[i], principals), sLe(userupdateinput0A[i], principals), sLe(userupdateinput1I[i], principals), sLe(userupdateinput1A[i], principals)) for i in range(n)]
s.add(userrange1)
userrange2 = [And(0 <= userOH[i]) for i in range(n)]
s.add(userrange2)
userrange3 = sLe(userOH, principals)
s.add(userrange3)
#FieldT: A
s.add(cLeH(AmakeOffer1outputC, AOH))
s.add(sIntegrity(AmakeOffer1outputI, Aqs, AOH))
s.add(availabilityP(AmakeOffer1outputA, Aqs, AOH))
s.add(cIntegrityE(AmakeOffer1input0I, Aqc))
s.add(lableLe(AmakeOffer1input0C, AmakeOffer1outputC, AmakeOffer1input0I, AmakeOffer1outputI, AmakeOffer1input0A, AmakeOffer1outputA))
s.add(cIntegrityE(AmakeOffer1input1I, Aqc))
s.add(lableLe(AmakeOffer1input1C, AmakeOffer1outputC, AmakeOffer1input1I, AmakeOffer1outputI, AmakeOffer1input1A, AmakeOffer1outputA))
s.add(cLeH(AmakeOffer2outputC, AOH))
s.add(sIntegrity(AmakeOffer2outputI, Aqs, AOH))
s.add(availabilityP(AmakeOffer2outputA, Aqs, AOH))
s.add(cIntegrityE(AmakeOffer2input0I, Aqc))
s.add(lableLe(AmakeOffer2input0C, AmakeOffer2outputC, AmakeOffer2input0I, AmakeOffer2outputI, AmakeOffer2input0A, AmakeOffer2outputA))
s.add(cIntegrityE(AmakeOffer2input1I, Aqc))
s.add(lableLe(AmakeOffer2input1C, AmakeOffer2outputC, AmakeOffer2input1I, AmakeOffer2outputI, AmakeOffer2input1A, AmakeOffer2outputA))
#FieldT: B
s.add(cLeH(BmakeOffer1outputC, BOH))
s.add(sIntegrity(BmakeOffer1outputI, Bqs, BOH))
s.add(availabilityP(BmakeOffer1outputA, Bqs, BOH))
s.add(cIntegrityE(BmakeOffer1input0I, Bqc))
s.add(lableLe(BmakeOffer1input0C, BmakeOffer1outputC, BmakeOffer1input0I, BmakeOffer1outputI, BmakeOffer1input0A, BmakeOffer1outputA))
s.add(cIntegrityE(BmakeOffer1input1I, Bqc))
s.add(lableLe(BmakeOffer1input1C, BmakeOffer1outputC, BmakeOffer1input1I, BmakeOffer1outputI, BmakeOffer1input1A, BmakeOffer1outputA))
s.add(cLeH(BmakeOffer2outputC, BOH))
s.add(sIntegrity(BmakeOffer2outputI, Bqs, BOH))
s.add(availabilityP(BmakeOffer2outputA, Bqs, BOH))
s.add(cIntegrityE(BmakeOffer2input0I, Bqc))
s.add(lableLe(BmakeOffer2input0C, BmakeOffer2outputC, BmakeOffer2input0I, BmakeOffer2outputI, BmakeOffer2input0A, BmakeOffer2outputA))
s.add(cIntegrityE(BmakeOffer2input1I, Bqc))
s.add(lableLe(BmakeOffer2input1C, BmakeOffer2outputC, BmakeOffer2input1I, BmakeOffer2outputI, BmakeOffer2input1A, BmakeOffer2outputA))
#FieldT: user
s.add(cLeH(userdeclareWinneroutputC, userOH))
s.add(sIntegrity(userdeclareWinneroutputI, userqs, userOH))
s.add(availabilityP(userdeclareWinneroutputA, userqs, userOH))
s.add(cIntegrityE(userdeclareWinnerinput0I, userqc))
s.add(lableLe(userdeclareWinnerinput0C, userdeclareWinneroutputC, userdeclareWinnerinput0I, userdeclareWinneroutputI, userdeclareWinnerinput0A, userdeclareWinneroutputA))
s.add(cLeH(userreadoutputC, userOH))
s.add(sIntegrity(userreadoutputI, userqs, userOH))
s.add(availabilityP(userreadoutputA, userqs, userOH))
s.add(cIntegrityE(userreadbotI, userqc))
s.add(lableLe(userreadbotC, userreadoutputC, userreadbotI, userreadoutputI, userreadbotA, userreadoutputA))
s.add(cLeH(userupdateoutputC, userOH))
s.add(sIntegrity(userupdateoutputI, userqs, userOH))
s.add(availabilityP(userupdateoutputA, userqs, userOH))
s.add(cIntegrityE(userupdateinput0I, userqc))
s.add(lableLe(userupdateinput0C, userupdateoutputC, userupdateinput0I, userupdateoutputI, userupdateinput0A, userupdateoutputA))
s.add(cIntegrityE(userupdateinput1I, userqc))
s.add(lableLe(userupdateinput1C, userupdateoutputC, userupdateinput1I, userupdateoutputI, userupdateinput1A, userupdateoutputA))
#MethodT: m8
#ObjCallT: let u = user.read() in let seatInfoA = A.makeOffer1(u, o) in let offerA = A.makeOffer2(u, o) in let x26 = user.update(seatInfoA, offerA) in If ((o < offerA)) then (let x5 = user.declareWinner(o) in this.ret(x5)) else (let seatInfoB = B.makeOffer1(u, offerA) in let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13)))
s.add(cLeH(userreadoutputC, m8H))
s.add(availabilityP(userreadbotA, userqc, m8H))
#ThisCallT: this.m7(u, o)
s.add(cLe(m8conxtC, m7conxtC))
s.add(bLe(m7conxtI, m8conxtI))
s.add(bLe(m7conxtA, m8conxtA))
s.add(cLe(userreadoutputC, m7uC))
s.add(cLe(botC, m7uC))
s.add(bLe(m7uI, userreadoutputI))
s.add(bLe(m7uI, botI))
s.add(bLe(m7uA, userreadoutputA))
s.add(bLe(m7uA, botA))
s.add(cLe(m8conxtC, m7uC))
s.add(bLe(m7uI, m8conxtI))
s.add(bLe(m7uA, m8conxtA))
s.add(availabilityP(m7uA, m7Q, m8H))
s.add(cLe(m8oC, m7oC))
s.add(cLe(oC, m7oC))
s.add(bLe(m7oI, m8oI))
s.add(bLe(m7oI, botI))
s.add(bLe(m7oA, m8oA))
s.add(bLe(m7oA, botA))
s.add(cLe(m8conxtC, m7oC))
s.add(bLe(m7oI, m8conxtI))
s.add(bLe(m7oA, m8conxtA))
s.add(availabilityP(m7oA, m7Q, m8H))
s.add(cIntegrityE(m8oI, m8Q))
s.add(cLeH(m8oC, m8H))
s.add(cLeH(m8conxtC, m8H))
#MethodT: m7
#ObjCallT: let seatInfoA = A.makeOffer1(u, o) in let offerA = A.makeOffer2(u, o) in let x26 = user.update(seatInfoA, offerA) in If ((o < offerA)) then (let x5 = user.declareWinner(o) in this.ret(x5)) else (let seatInfoB = B.makeOffer1(u, offerA) in let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13)))
s.add(cLeH(AmakeOffer1outputC, m7H))
s.add(cLe(m7uC, AmakeOffer1input0C))
s.add(cLe(botC, AmakeOffer1input0C))
s.add(bLe(AmakeOffer1input0I, m7uI))
s.add(bLe(AmakeOffer1input0I, botI))
s.add(bLe(AmakeOffer1input0A, m7uA))
s.add(bLe(AmakeOffer1input0A, botA))
s.add(availabilityP(AmakeOffer1input0A, Aqc, m7H))
s.add(cLe(m7oC, AmakeOffer1input1C))
s.add(cLe(oC, AmakeOffer1input1C))
s.add(bLe(AmakeOffer1input1I, m7oI))
s.add(bLe(AmakeOffer1input1I, botI))
s.add(bLe(AmakeOffer1input1A, m7oA))
s.add(bLe(AmakeOffer1input1A, botA))
s.add(availabilityP(AmakeOffer1input1A, Aqc, m7H))
#ThisCallT: this.m6(u, seatInfoA, o)
s.add(cLe(m7conxtC, m6conxtC))
s.add(bLe(m6conxtI, m7conxtI))
s.add(bLe(m6conxtA, m7conxtA))
s.add(cLe(m7uC, m6uC))
s.add(cLe(botC, m6uC))
s.add(cLe(botC, m6uC))
s.add(bLe(m6uI, m7uI))
s.add(bLe(m6uI, botI))
s.add(bLe(m6uI, botI))
s.add(bLe(m6uA, m7uA))
s.add(bLe(m6uA, botA))
s.add(bLe(m6uA, botA))
s.add(cLe(m7conxtC, m6uC))
s.add(bLe(m6uI, m7conxtI))
s.add(bLe(m6uA, m7conxtA))
s.add(availabilityP(m6uA, m6Q, m7H))
s.add(cLe(AmakeOffer1outputC, m6seatInfoAC))
s.add(cLe(botC, m6seatInfoAC))
s.add(bLe(m6seatInfoAI, AmakeOffer1outputI))
s.add(bLe(m6seatInfoAI, botI))
s.add(bLe(m6seatInfoAA, AmakeOffer1outputA))
s.add(bLe(m6seatInfoAA, botA))
s.add(cLe(m7conxtC, m6seatInfoAC))
s.add(bLe(m6seatInfoAI, m7conxtI))
s.add(bLe(m6seatInfoAA, m7conxtA))
s.add(availabilityP(m6seatInfoAA, m6Q, m7H))
s.add(cLe(m7oC, m6oC))
s.add(cLe(oC, m6oC))
s.add(cLe(oC, m6oC))
s.add(bLe(m6oI, m7oI))
s.add(bLe(m6oI, botI))
s.add(bLe(m6oI, botI))
s.add(bLe(m6oA, m7oA))
s.add(bLe(m6oA, botA))
s.add(bLe(m6oA, botA))
s.add(cLe(m7conxtC, m6oC))
s.add(bLe(m6oI, m7conxtI))
s.add(bLe(m6oA, m7conxtA))
s.add(availabilityP(m6oA, m6Q, m7H))
s.add(cIntegrityE(m7uI, m7Q))
s.add(cIntegrityE(m7oI, m7Q))
s.add(cLeH(m7uC, m7H))
s.add(cLeH(m7oC, m7H))
s.add(cLeH(m7conxtC, m7H))
#MethodT: m6
#ObjCallT: let offerA = A.makeOffer2(u, o) in let x26 = user.update(seatInfoA, offerA) in If ((o < offerA)) then (let x5 = user.declareWinner(o) in this.ret(x5)) else (let seatInfoB = B.makeOffer1(u, offerA) in let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13)))
s.add(cLeH(AmakeOffer2outputC, m6H))
s.add(cLe(m6uC, AmakeOffer2input0C))
s.add(cLe(botC, AmakeOffer2input0C))
s.add(bLe(AmakeOffer2input0I, m6uI))
s.add(bLe(AmakeOffer2input0I, botI))
s.add(bLe(AmakeOffer2input0A, m6uA))
s.add(bLe(AmakeOffer2input0A, botA))
s.add(availabilityP(AmakeOffer2input0A, Aqc, m6H))
s.add(cLe(m6oC, AmakeOffer2input1C))
s.add(cLe(oC, AmakeOffer2input1C))
s.add(bLe(AmakeOffer2input1I, m6oI))
s.add(bLe(AmakeOffer2input1I, botI))
s.add(bLe(AmakeOffer2input1A, m6oA))
s.add(bLe(AmakeOffer2input1A, botA))
s.add(availabilityP(AmakeOffer2input1A, Aqc, m6H))
#ThisCallT: this.m5(offerA, u, seatInfoA, o)
s.add(cLe(m6conxtC, m5conxtC))
s.add(bLe(m5conxtI, m6conxtI))
s.add(bLe(m5conxtA, m6conxtA))
s.add(cLe(AmakeOffer2outputC, m5offerAC))
s.add(cLe(botC, m5offerAC))
s.add(bLe(m5offerAI, AmakeOffer2outputI))
s.add(bLe(m5offerAI, botI))
s.add(bLe(m5offerAA, AmakeOffer2outputA))
s.add(bLe(m5offerAA, botA))
s.add(cLe(m6conxtC, m5offerAC))
s.add(bLe(m5offerAI, m6conxtI))
s.add(bLe(m5offerAA, m6conxtA))
s.add(availabilityP(m5offerAA, m5Q, m6H))
s.add(cLe(m6uC, m5uC))
s.add(cLe(botC, m5uC))
s.add(cLe(botC, m5uC))
s.add(bLe(m5uI, m6uI))
s.add(bLe(m5uI, botI))
s.add(bLe(m5uI, botI))
s.add(bLe(m5uA, m6uA))
s.add(bLe(m5uA, botA))
s.add(bLe(m5uA, botA))
s.add(cLe(m6conxtC, m5uC))
s.add(bLe(m5uI, m6conxtI))
s.add(bLe(m5uA, m6conxtA))
s.add(availabilityP(m5uA, m5Q, m6H))
s.add(cLe(m6seatInfoAC, m5seatInfoAC))
s.add(cLe(botC, m5seatInfoAC))
s.add(bLe(m5seatInfoAI, m6seatInfoAI))
s.add(bLe(m5seatInfoAI, botI))
s.add(bLe(m5seatInfoAA, m6seatInfoAA))
s.add(bLe(m5seatInfoAA, botA))
s.add(cLe(m6conxtC, m5seatInfoAC))
s.add(bLe(m5seatInfoAI, m6conxtI))
s.add(bLe(m5seatInfoAA, m6conxtA))
s.add(availabilityP(m5seatInfoAA, m5Q, m6H))
s.add(cLe(m6oC, m5oC))
s.add(cLe(oC, m5oC))
s.add(cLe(oC, m5oC))
s.add(bLe(m5oI, m6oI))
s.add(bLe(m5oI, botI))
s.add(bLe(m5oI, botI))
s.add(bLe(m5oA, m6oA))
s.add(bLe(m5oA, botA))
s.add(bLe(m5oA, botA))
s.add(cLe(m6conxtC, m5oC))
s.add(bLe(m5oI, m6conxtI))
s.add(bLe(m5oA, m6conxtA))
s.add(availabilityP(m5oA, m5Q, m6H))
s.add(cIntegrityE(m6uI, m6Q))
s.add(cIntegrityE(m6seatInfoAI, m6Q))
s.add(cIntegrityE(m6oI, m6Q))
s.add(cLeH(m6uC, m6H))
s.add(cLeH(m6seatInfoAC, m6H))
s.add(cLeH(m6oC, m6H))
s.add(cLeH(m6conxtC, m6H))
#MethodT: m5
#ObjCallT: let x26 = user.update(seatInfoA, offerA) in If ((o < offerA)) then (let x5 = user.declareWinner(o) in this.ret(x5)) else (let seatInfoB = B.makeOffer1(u, offerA) in let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13)))
s.add(cLeH(userupdateoutputC, m5H))
s.add(cLe(m5seatInfoAC, userupdateinput0C))
s.add(cLe(botC, userupdateinput0C))
s.add(bLe(userupdateinput0I, m5seatInfoAI))
s.add(bLe(userupdateinput0I, botI))
s.add(bLe(userupdateinput0A, m5seatInfoAA))
s.add(bLe(userupdateinput0A, botA))
s.add(availabilityP(userupdateinput0A, userqc, m5H))
s.add(cLe(m5offerAC, userupdateinput1C))
s.add(cLe(botC, userupdateinput1C))
s.add(bLe(userupdateinput1I, m5offerAI))
s.add(bLe(userupdateinput1I, botI))
s.add(bLe(userupdateinput1A, m5offerAA))
s.add(bLe(userupdateinput1A, botA))
s.add(availabilityP(userupdateinput1A, userqc, m5H))
#IfT: If ((o < offerA)) then (this.m0(o)) else (this.m4(offerA, u))
#ThisCallT: this.m0(o)
s.add(cLe(m5conxtC, m0conxtC))
s.add(cLe(m5oC, m0conxtC))
s.add(cLe(oC, m0conxtC))
s.add(cLe(m5offerAC, m0conxtC))
s.add(cLe(botC, m0conxtC))
s.add(cLe(botC, m0conxtC))
s.add(cLe(m5oC, m0conxtC))
s.add(cLe(oC, m0conxtC))
s.add(cLe(m5offerAC, m0conxtC))
s.add(cLe(botC, m0conxtC))
s.add(cLe(botC, m0conxtC))
s.add(bLe(m0conxtI, m5conxtI))
s.add(bLe(m0conxtI, m5oI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtI, m5offerAI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtI, m5oI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtI, m5offerAI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtI, botI))
s.add(bLe(m0conxtA, m5conxtA))
s.add(bLe(m0conxtA, m5oA))
s.add(bLe(m0conxtA, botA))
s.add(bLe(m0conxtA, m5offerAA))
s.add(bLe(m0conxtA, botA))
s.add(bLe(m0conxtA, botA))
s.add(bLe(m0conxtA, m5oA))
s.add(bLe(m0conxtA, botA))
s.add(bLe(m0conxtA, m5offerAA))
s.add(bLe(m0conxtA, botA))
s.add(bLe(m0conxtA, botA))
s.add(cLe(m5oC, m0oC))
s.add(cLe(oC, m0oC))
s.add(cLe(oC, m0oC))
s.add(bLe(m0oI, m5oI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oA, m5oA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, botA))
s.add(cLe(m5conxtC, m0oC))
s.add(cLe(m5oC, m0oC))
s.add(cLe(oC, m0oC))
s.add(cLe(m5offerAC, m0oC))
s.add(cLe(botC, m0oC))
s.add(cLe(botC, m0oC))
s.add(cLe(m5oC, m0oC))
s.add(cLe(oC, m0oC))
s.add(cLe(m5offerAC, m0oC))
s.add(cLe(botC, m0oC))
s.add(cLe(botC, m0oC))
s.add(bLe(m0oI, m5conxtI))
s.add(bLe(m0oI, m5oI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, m5offerAI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, m5oI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, m5offerAI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oI, botI))
s.add(bLe(m0oA, m5conxtA))
s.add(bLe(m0oA, m5oA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, m5offerAA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, m5oA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, m5offerAA))
s.add(bLe(m0oA, botA))
s.add(bLe(m0oA, botA))
s.add(availabilityP(m0oA, m0Q, m5H))
#ThisCallT: this.m4(offerA, u)
s.add(cLe(m5conxtC, m4conxtC))
s.add(cLe(m5oC, m4conxtC))
s.add(cLe(oC, m4conxtC))
s.add(cLe(m5offerAC, m4conxtC))
s.add(cLe(botC, m4conxtC))
s.add(cLe(botC, m4conxtC))
s.add(cLe(m5oC, m4conxtC))
s.add(cLe(oC, m4conxtC))
s.add(cLe(m5offerAC, m4conxtC))
s.add(cLe(botC, m4conxtC))
s.add(cLe(botC, m4conxtC))
s.add(bLe(m4conxtI, m5conxtI))
s.add(bLe(m4conxtI, m5oI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtI, m5offerAI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtI, m5oI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtI, m5offerAI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtI, botI))
s.add(bLe(m4conxtA, m5conxtA))
s.add(bLe(m4conxtA, m5oA))
s.add(bLe(m4conxtA, botA))
s.add(bLe(m4conxtA, m5offerAA))
s.add(bLe(m4conxtA, botA))
s.add(bLe(m4conxtA, botA))
s.add(bLe(m4conxtA, m5oA))
s.add(bLe(m4conxtA, botA))
s.add(bLe(m4conxtA, m5offerAA))
s.add(bLe(m4conxtA, botA))
s.add(bLe(m4conxtA, botA))
s.add(cLe(m5offerAC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(bLe(m4offerAI, m5offerAI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAA, m5offerAA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, botA))
s.add(cLe(m5conxtC, m4offerAC))
s.add(cLe(m5oC, m4offerAC))
s.add(cLe(oC, m4offerAC))
s.add(cLe(m5offerAC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(cLe(m5oC, m4offerAC))
s.add(cLe(oC, m4offerAC))
s.add(cLe(m5offerAC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(cLe(botC, m4offerAC))
s.add(bLe(m4offerAI, m5conxtI))
s.add(bLe(m4offerAI, m5oI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, m5offerAI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, m5oI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, m5offerAI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAI, botI))
s.add(bLe(m4offerAA, m5conxtA))
s.add(bLe(m4offerAA, m5oA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, m5offerAA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, m5oA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, m5offerAA))
s.add(bLe(m4offerAA, botA))
s.add(bLe(m4offerAA, botA))
s.add(availabilityP(m4offerAA, m4Q, m5H))
s.add(cLe(m5uC, m4uC))
s.add(cLe(botC, m4uC))
s.add(bLe(m4uI, m5uI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uA, m5uA))
s.add(bLe(m4uA, botA))
s.add(cLe(m5conxtC, m4uC))
s.add(cLe(m5oC, m4uC))
s.add(cLe(oC, m4uC))
s.add(cLe(m5offerAC, m4uC))
s.add(cLe(botC, m4uC))
s.add(cLe(botC, m4uC))
s.add(cLe(m5oC, m4uC))
s.add(cLe(oC, m4uC))
s.add(cLe(m5offerAC, m4uC))
s.add(cLe(botC, m4uC))
s.add(cLe(botC, m4uC))
s.add(bLe(m4uI, m5conxtI))
s.add(bLe(m4uI, m5oI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uI, m5offerAI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uI, m5oI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uI, m5offerAI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uI, botI))
s.add(bLe(m4uA, m5conxtA))
s.add(bLe(m4uA, m5oA))
s.add(bLe(m4uA, botA))
s.add(bLe(m4uA, m5offerAA))
s.add(bLe(m4uA, botA))
s.add(bLe(m4uA, botA))
s.add(bLe(m4uA, m5oA))
s.add(bLe(m4uA, botA))
s.add(bLe(m4uA, m5offerAA))
s.add(bLe(m4uA, botA))
s.add(bLe(m4uA, botA))
s.add(availabilityP(m4uA, m4Q, m5H))
s.add(cIntegrityE(m5offerAI, m5Q))
s.add(cIntegrityE(m5uI, m5Q))
s.add(cIntegrityE(m5seatInfoAI, m5Q))
s.add(cIntegrityE(m5oI, m5Q))
s.add(cLeH(m5offerAC, m5H))
s.add(cLeH(m5uC, m5H))
s.add(cLeH(m5seatInfoAC, m5H))
s.add(cLeH(m5oC, m5H))
s.add(cLeH(m5conxtC, m5H))
#MethodT: m4
#ObjCallT: let seatInfoB = B.makeOffer1(u, offerA) in let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13))
s.add(cLeH(BmakeOffer1outputC, m4H))
s.add(cLe(m4uC, BmakeOffer1input0C))
s.add(cLe(botC, BmakeOffer1input0C))
s.add(bLe(BmakeOffer1input0I, m4uI))
s.add(bLe(BmakeOffer1input0I, botI))
s.add(bLe(BmakeOffer1input0A, m4uA))
s.add(bLe(BmakeOffer1input0A, botA))
s.add(availabilityP(BmakeOffer1input0A, Bqc, m4H))
s.add(cLe(m4offerAC, BmakeOffer1input1C))
s.add(cLe(botC, BmakeOffer1input1C))
s.add(bLe(BmakeOffer1input1I, m4offerAI))
s.add(bLe(BmakeOffer1input1I, botI))
s.add(bLe(BmakeOffer1input1A, m4offerAA))
s.add(bLe(BmakeOffer1input1A, botA))
s.add(availabilityP(BmakeOffer1input1A, Bqc, m4H))
#ThisCallT: this.m3(offerA, seatInfoB, u)
s.add(cLe(m4conxtC, m3conxtC))
s.add(bLe(m3conxtI, m4conxtI))
s.add(bLe(m3conxtA, m4conxtA))
s.add(cLe(m4offerAC, m3offerAC))
s.add(cLe(botC, m3offerAC))
s.add(cLe(botC, m3offerAC))
s.add(bLe(m3offerAI, m4offerAI))
s.add(bLe(m3offerAI, botI))
s.add(bLe(m3offerAI, botI))
s.add(bLe(m3offerAA, m4offerAA))
s.add(bLe(m3offerAA, botA))
s.add(bLe(m3offerAA, botA))
s.add(cLe(m4conxtC, m3offerAC))
s.add(bLe(m3offerAI, m4conxtI))
s.add(bLe(m3offerAA, m4conxtA))
s.add(availabilityP(m3offerAA, m3Q, m4H))
s.add(cLe(BmakeOffer1outputC, m3seatInfoBC))
s.add(cLe(botC, m3seatInfoBC))
s.add(bLe(m3seatInfoBI, BmakeOffer1outputI))
s.add(bLe(m3seatInfoBI, botI))
s.add(bLe(m3seatInfoBA, BmakeOffer1outputA))
s.add(bLe(m3seatInfoBA, botA))
s.add(cLe(m4conxtC, m3seatInfoBC))
s.add(bLe(m3seatInfoBI, m4conxtI))
s.add(bLe(m3seatInfoBA, m4conxtA))
s.add(availabilityP(m3seatInfoBA, m3Q, m4H))
s.add(cLe(m4uC, m3uC))
s.add(cLe(botC, m3uC))
s.add(cLe(botC, m3uC))
s.add(bLe(m3uI, m4uI))
s.add(bLe(m3uI, botI))
s.add(bLe(m3uI, botI))
s.add(bLe(m3uA, m4uA))
s.add(bLe(m3uA, botA))
s.add(bLe(m3uA, botA))
s.add(cLe(m4conxtC, m3uC))
s.add(bLe(m3uI, m4conxtI))
s.add(bLe(m3uA, m4conxtA))
s.add(availabilityP(m3uA, m3Q, m4H))
s.add(cIntegrityE(m4offerAI, m4Q))
s.add(cIntegrityE(m4uI, m4Q))
s.add(cLeH(m4offerAC, m4H))
s.add(cLeH(m4uC, m4H))
s.add(cLeH(m4conxtC, m4H))
#MethodT: m3
#ObjCallT: let offerB = B.makeOffer2(u, offerA) in let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13))
s.add(cLeH(BmakeOffer2outputC, m3H))
s.add(cLe(m3uC, BmakeOffer2input0C))
s.add(cLe(botC, BmakeOffer2input0C))
s.add(bLe(BmakeOffer2input0I, m3uI))
s.add(bLe(BmakeOffer2input0I, botI))
s.add(bLe(BmakeOffer2input0A, m3uA))
s.add(bLe(BmakeOffer2input0A, botA))
s.add(availabilityP(BmakeOffer2input0A, Bqc, m3H))
s.add(cLe(m3offerAC, BmakeOffer2input1C))
s.add(cLe(botC, BmakeOffer2input1C))
s.add(bLe(BmakeOffer2input1I, m3offerAI))
s.add(bLe(BmakeOffer2input1I, botI))
s.add(bLe(BmakeOffer2input1A, m3offerAA))
s.add(bLe(BmakeOffer2input1A, botA))
s.add(availabilityP(BmakeOffer2input1A, Bqc, m3H))
#ThisCallT: this.m2(offerB, offerA, seatInfoB)
s.add(cLe(m3conxtC, m2conxtC))
s.add(bLe(m2conxtI, m3conxtI))
s.add(bLe(m2conxtA, m3conxtA))
s.add(cLe(BmakeOffer2outputC, m2offerBC))
s.add(cLe(botC, m2offerBC))
s.add(bLe(m2offerBI, BmakeOffer2outputI))
s.add(bLe(m2offerBI, botI))
s.add(bLe(m2offerBA, BmakeOffer2outputA))
s.add(bLe(m2offerBA, botA))
s.add(cLe(m3conxtC, m2offerBC))
s.add(bLe(m2offerBI, m3conxtI))
s.add(bLe(m2offerBA, m3conxtA))
s.add(availabilityP(m2offerBA, m2Q, m3H))
s.add(cLe(m3offerAC, m2offerAC))
s.add(cLe(botC, m2offerAC))
s.add(cLe(botC, m2offerAC))
s.add(bLe(m2offerAI, m3offerAI))
s.add(bLe(m2offerAI, botI))
s.add(bLe(m2offerAI, botI))
s.add(bLe(m2offerAA, m3offerAA))
s.add(bLe(m2offerAA, botA))
s.add(bLe(m2offerAA, botA))
s.add(cLe(m3conxtC, m2offerAC))
s.add(bLe(m2offerAI, m3conxtI))
s.add(bLe(m2offerAA, m3conxtA))
s.add(availabilityP(m2offerAA, m2Q, m3H))
s.add(cLe(m3seatInfoBC, m2seatInfoBC))
s.add(cLe(botC, m2seatInfoBC))
s.add(bLe(m2seatInfoBI, m3seatInfoBI))
s.add(bLe(m2seatInfoBI, botI))
s.add(bLe(m2seatInfoBA, m3seatInfoBA))
s.add(bLe(m2seatInfoBA, botA))
s.add(cLe(m3conxtC, m2seatInfoBC))
s.add(bLe(m2seatInfoBI, m3conxtI))
s.add(bLe(m2seatInfoBA, m3conxtA))
s.add(availabilityP(m2seatInfoBA, m2Q, m3H))
s.add(cIntegrityE(m3offerAI, m3Q))
s.add(cIntegrityE(m3seatInfoBI, m3Q))
s.add(cIntegrityE(m3uI, m3Q))
s.add(cLeH(m3offerAC, m3H))
s.add(cLeH(m3seatInfoBC, m3H))
s.add(cLeH(m3uC, m3H))
s.add(cLeH(m3conxtC, m3H))
#MethodT: m2
#ObjCallT: let x17 = user.update(seatInfoB, offerB) in If ((offerA >= offerB)) then (this.m8(offerB)) else (let x13 = user.declareWinner(offerA) in this.ret(x13))
s.add(cLeH(userupdateoutputC, m2H))
s.add(cLe(m2seatInfoBC, userupdateinput0C))
s.add(cLe(botC, userupdateinput0C))
s.add(bLe(userupdateinput0I, m2seatInfoBI))
s.add(bLe(userupdateinput0I, botI))
s.add(bLe(userupdateinput0A, m2seatInfoBA))
s.add(bLe(userupdateinput0A, botA))
s.add(availabilityP(userupdateinput0A, userqc, m2H))
s.add(cLe(m2offerBC, userupdateinput1C))
s.add(cLe(botC, userupdateinput1C))
s.add(bLe(userupdateinput1I, m2offerBI))
s.add(bLe(userupdateinput1I, botI))
s.add(bLe(userupdateinput1A, m2offerBA))
s.add(bLe(userupdateinput1A, botA))
s.add(availabilityP(userupdateinput1A, userqc, m2H))
#IfT: If ((offerA >= offerB)) then (this.m8(offerB)) else (this.m1(offerA))
#ThisCallT: this.m8(offerB)
s.add(cLe(m2conxtC, m8conxtC))
s.add(cLe(m2offerAC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(cLe(m2offerBC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(cLe(m2offerAC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(cLe(m2offerBC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(cLe(botC, m8conxtC))
s.add(bLe(m8conxtI, m2conxtI))
s.add(bLe(m8conxtI, m2offerAI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtI, m2offerBI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtI, m2offerAI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtI, m2offerBI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtI, botI))
s.add(bLe(m8conxtA, m2conxtA))
s.add(bLe(m8conxtA, m2offerAA))
s.add(bLe(m8conxtA, botA))
s.add(bLe(m8conxtA, m2offerBA))
s.add(bLe(m8conxtA, botA))
s.add(bLe(m8conxtA, botA))
s.add(bLe(m8conxtA, m2offerAA))
s.add(bLe(m8conxtA, botA))
s.add(bLe(m8conxtA, m2offerBA))
s.add(bLe(m8conxtA, botA))
s.add(bLe(m8conxtA, botA))
s.add(cLe(m2offerBC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(botC, m8oC))
s.add(bLe(m8oI, m2offerBI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oA, m2offerBA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, botA))
s.add(cLe(m2conxtC, m8oC))
s.add(cLe(m2offerAC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(m2offerBC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(m2offerAC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(m2offerBC, m8oC))
s.add(cLe(botC, m8oC))
s.add(cLe(botC, m8oC))
s.add(bLe(m8oI, m2conxtI))
s.add(bLe(m8oI, m2offerAI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, m2offerBI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, m2offerAI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, m2offerBI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oA, m2conxtA))
s.add(bLe(m8oA, m2offerAA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, m2offerBA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, m2offerAA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, m2offerBA))
s.add(bLe(m8oA, botA))
s.add(bLe(m8oA, botA))
s.add(availabilityP(m8oA, m8Q, m2H))
#ThisCallT: this.m1(offerA)
s.add(cLe(m2conxtC, m1conxtC))
s.add(cLe(m2offerAC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(cLe(m2offerBC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(cLe(m2offerAC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(cLe(m2offerBC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(cLe(botC, m1conxtC))
s.add(bLe(m1conxtI, m2conxtI))
s.add(bLe(m1conxtI, m2offerAI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtI, m2offerBI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtI, m2offerAI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtI, m2offerBI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtI, botI))
s.add(bLe(m1conxtA, m2conxtA))
s.add(bLe(m1conxtA, m2offerAA))
s.add(bLe(m1conxtA, botA))
s.add(bLe(m1conxtA, m2offerBA))
s.add(bLe(m1conxtA, botA))
s.add(bLe(m1conxtA, botA))
s.add(bLe(m1conxtA, m2offerAA))
s.add(bLe(m1conxtA, botA))
s.add(bLe(m1conxtA, m2offerBA))
s.add(bLe(m1conxtA, botA))
s.add(bLe(m1conxtA, botA))
s.add(cLe(m2offerAC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(bLe(m1offerAI, m2offerAI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAA, m2offerAA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, botA))
s.add(cLe(m2conxtC, m1offerAC))
s.add(cLe(m2offerAC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(m2offerBC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(m2offerAC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(m2offerBC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(cLe(botC, m1offerAC))
s.add(bLe(m1offerAI, m2conxtI))
s.add(bLe(m1offerAI, m2offerAI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, m2offerBI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, m2offerAI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, m2offerBI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAI, botI))
s.add(bLe(m1offerAA, m2conxtA))
s.add(bLe(m1offerAA, m2offerAA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, m2offerBA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, m2offerAA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, m2offerBA))
s.add(bLe(m1offerAA, botA))
s.add(bLe(m1offerAA, botA))
s.add(availabilityP(m1offerAA, m1Q, m2H))
s.add(cIntegrityE(m2offerBI, m2Q))
s.add(cIntegrityE(m2offerAI, m2Q))
s.add(cIntegrityE(m2seatInfoBI, m2Q))
s.add(cLeH(m2offerBC, m2H))
s.add(cLeH(m2offerAC, m2H))
s.add(cLeH(m2seatInfoBC, m2H))
s.add(cLeH(m2conxtC, m2H))
#MethodT: m1
#ObjCallT: let x13 = user.declareWinner(offerA) in this.ret(x13)
s.add(cLeH(userdeclareWinneroutputC, m1H))
s.add(cLe(m1offerAC, userdeclareWinnerinput0C))
s.add(cLe(botC, userdeclareWinnerinput0C))
s.add(bLe(userdeclareWinnerinput0I, m1offerAI))
s.add(bLe(userdeclareWinnerinput0I, botI))
s.add(bLe(userdeclareWinnerinput0A, m1offerAA))
s.add(bLe(userdeclareWinnerinput0A, botA))
s.add(availabilityP(userdeclareWinnerinput0A, userqc, m1H))
#ThisCallT: this.ret(x13)
s.add(cLe(m1conxtC, resultC))
s.add(bLe(resultI, m1conxtI))
s.add(bLe(resultA, m1conxtA))
s.add(cLe(userdeclareWinneroutputC, resultC))
s.add(cLe(botC, resultC))
s.add(bLe(resultI, userdeclareWinneroutputI))
s.add(bLe(resultI, botI))
s.add(bLe(resultA, userdeclareWinneroutputA))
s.add(bLe(resultA, botA))
s.add(cLe(m1conxtC, resultC))
s.add(bLe(resultI, m1conxtI))
s.add(bLe(resultA, m1conxtA))
s.add(availabilityP(resultA, resQ, m1H))
s.add(cIntegrityE(m1offerAI, m1Q))
s.add(cLeH(m1offerAC, m1H))
s.add(cLeH(m1conxtC, m1H))
#MethodT: m0
#ObjCallT: let x5 = user.declareWinner(o) in this.ret(x5)
s.add(cLeH(userdeclareWinneroutputC, m0H))
s.add(cLe(m0oC, userdeclareWinnerinput0C))
s.add(cLe(oC, userdeclareWinnerinput0C))
s.add(bLe(userdeclareWinnerinput0I, m0oI))
s.add(bLe(userdeclareWinnerinput0I, botI))
s.add(bLe(userdeclareWinnerinput0A, m0oA))
s.add(bLe(userdeclareWinnerinput0A, botA))
s.add(availabilityP(userdeclareWinnerinput0A, userqc, m0H))
#ThisCallT: this.ret(x5)
s.add(cLe(m0conxtC, resultC))
s.add(bLe(resultI, m0conxtI))
s.add(bLe(resultA, m0conxtA))
s.add(cLe(userdeclareWinneroutputC, resultC))
s.add(cLe(botC, resultC))
s.add(bLe(resultI, userdeclareWinneroutputI))
s.add(bLe(resultI, botI))
s.add(bLe(resultA, userdeclareWinneroutputA))
s.add(bLe(resultA, botA))
s.add(cLe(m0conxtC, resultC))
s.add(bLe(resultI, m0conxtI))
s.add(bLe(resultA, m0conxtA))
s.add(availabilityP(resultA, resQ, m0H))
s.add(cIntegrityE(m0oI, m0Q))
s.add(cLeH(m0oC, m0H))
s.add(cLeH(m0conxtC, m0H))
#MethodT: ret
s.add(cLeH(resultC, resH))
s.add(cIntegrityE(resultI, resQ))
s.add(cLe(startC, m8conxtC))
s.add(bLe(m8conxtI, startI))
s.add(bLe(m8conxtA, startA))
s.add(cLe(startC, m8oC))
s.add(bLe(m8oI, startI))
s.add(bLe(m8oA, startA))
s.add(cLe(oC, m8oC))
s.add(bLe(m8oI, botI))
s.add(bLe(m8oA, botA))
s.add(availabilityP(m8oA, m8Q, resH))
print("n = 3")
print("principals = [4, 7, 1]")
weight = [1, 1, 12]

s.minimize(sum(m0H[i] * weight[i] for i in range(n)) + sum(m1H[i] * weight[i] for i in range(n)) + sum(m2H[i] * weight[i] for i in range(n)) + sum(m3H[i] * weight[i] for i in range(n)) + sum(m4H[i] * weight[i] for i in range(n)) + sum(m5H[i] * weight[i] for i in range(n)) + sum(m6H[i] * weight[i] for i in range(n)) + sum(m7H[i] * weight[i] for i in range(n)) + sum(m8H[i] * weight[i] for i in range(n)) + sum(AOH[i] * weight[i] for i in range(n)) + sum(BOH[i] * weight[i] for i in range(n)) + sum(userOH[i] * weight[i] for i in range(n)) + sum(Aqs[0][i] * weight[i] for i in range(n)) + sum(Aqs[1][i] * weight[i] for i in range(n)) + sum(Aqs[2][i] * weight[i] for i in range(n)) + sum(Bqs[0][i] * weight[i] for i in range(n)) + sum(Bqs[1][i] * weight[i] for i in range(n)) + sum(Bqs[2][i] * weight[i] for i in range(n)) + sum(userqs[0][i] * weight[i] for i in range(n)) + sum(userqs[1][i] * weight[i] for i in range(n)) + sum(userqs[2][i] * weight[i] for i in range(n)) + sum(Aqc[0][i] * weight[i] for i in range(n)) + sum(Aqc[1][i] * weight[i] for i in range(n)) + sum(Aqc[2][i] * weight[i] for i in range(n)) + sum(Bqc[0][i] * weight[i] for i in range(n)) + sum(Bqc[1][i] * weight[i] for i in range(n)) + sum(Bqc[2][i] * weight[i] for i in range(n)) + sum(userqc[0][i] * weight[i] for i in range(n)) + sum(userqc[1][i] * weight[i] for i in range(n)) + sum(userqc[2][i] * weight[i] for i in range(n)) + sum(resQ[0]) + sum(resQ[1]) + sum(resQ[2]) + sum(m0Q[0]) + sum(m0Q[1]) + sum(m0Q[2]) + sum(m1Q[0]) + sum(m1Q[1]) + sum(m1Q[2]) + sum(m2Q[0]) + sum(m2Q[1]) + sum(m2Q[2]) + sum(m3Q[0]) + sum(m3Q[1]) + sum(m3Q[2]) + sum(m4Q[0]) + sum(m4Q[1]) + sum(m4Q[2]) + sum(m5Q[0]) + sum(m5Q[1]) + sum(m5Q[2]) + sum(m6Q[0]) + sum(m6Q[1]) + sum(m6Q[2]) + sum(m7Q[0]) + sum(m7Q[1]) + sum(m7Q[2]) + sum(m8Q[0]) + sum(m8Q[1]) + sum(m8Q[2]))
print(s.check())
m = s.model()
print("resH:", resH)
print("m0H:", [m[hInfo].as_long() for hInfo in m0H])
print("m1H:", [m[hInfo].as_long() for hInfo in m1H])
print("m2H:", [m[hInfo].as_long() for hInfo in m2H])
print("m3H:", [m[hInfo].as_long() for hInfo in m3H])
print("m4H:", [m[hInfo].as_long() for hInfo in m4H])
print("m5H:", [m[hInfo].as_long() for hInfo in m5H])
print("m6H:", [m[hInfo].as_long() for hInfo in m6H])
print("m7H:", [m[hInfo].as_long() for hInfo in m7H])
print("m8H:", [m[hInfo].as_long() for hInfo in m8H])
print("resQ:", [m[e].as_long() for qs in resQ for e in qs])
print("m0Q:", [m[e].as_long() for qs in m0Q for e in qs])
print("m1Q:", [m[e].as_long() for qs in m1Q for e in qs])
print("m2Q:", [m[e].as_long() for qs in m2Q for e in qs])
print("m3Q:", [m[e].as_long() for qs in m3Q for e in qs])
print("m4Q:", [m[e].as_long() for qs in m4Q for e in qs])
print("m5Q:", [m[e].as_long() for qs in m5Q for e in qs])
print("m6Q:", [m[e].as_long() for qs in m6Q for e in qs])
print("m7Q:", [m[e].as_long() for qs in m7Q for e in qs])
print("m8Q:", [m[e].as_long() for qs in m8Q for e in qs])
print("Aqs:", [m[e].as_long() for qs in Aqs for e in qs])
print("Bqs:", [m[e].as_long() for qs in Bqs for e in qs])
print("userqs:", [m[e].as_long() for qs in userqs for e in qs])
print("Aqc:", [m[e].as_long() for qs in Aqc for e in qs])
print("Bqc:", [m[e].as_long() for qs in Bqc for e in qs])
print("userqc:", [m[e].as_long() for qs in userqc for e in qs])
print("AOH:", [m[hInfo].as_long() for hInfo in AOH])
print("BOH:", [m[hInfo].as_long() for hInfo in BOH])
print("userOH:", [m[hInfo].as_long() for hInfo in userOH])
endT = time.time() - startT
print(endT)
