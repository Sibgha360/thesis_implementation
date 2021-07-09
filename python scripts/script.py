import camelot
tablesA = camelot.read_pdf('/home/sibgha/thesis-files/adidas.pdf',  flavor='stream', pages='5',  strip_text=' .\n',  edge_tol=500, split_text=True)
tablesA[0].to_json('/home/sibgha/thesis-files/adidas.json')


tablesM = camelot.read_pdf('/home/sibgha/thesis-files/merck.pdf',  flavor='stream', pages='78',  strip_text='\n')
tablesM[0].to_json('/home/sibgha/thesis-files/merck1.json')
tablesM[1].to_json('/home/sibgha/thesis-files/merck2.json')
tablesM[2].to_json('/home/sibgha/thesis-files/merck3.json')


tablesP = camelot.read_pdf('/home/sibgha/thesis-files/puma.pdf',  flavor='stream', pages='1-end',  strip_text='\n',  edge_tol=500, split_text=True)
tablesP[0].to_json('/home/sibgha/thesis-files/puma1.json')
tablesP[1].to_json('/home/sibgha/thesis-files/puma2.json')
tablesP[2].to_json('/home/sibgha/thesis-files/puma3.json')
tablesP[3].to_json('/home/sibgha/thesis-files/puma4.json')

