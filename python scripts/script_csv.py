import camelot
tablesA = camelot.read_pdf('/home/sibgha/thesis-files/adidas.pdf',  flavor='stream', pages='5',  strip_text='\n', edge_tol=500, split_text=True)
tablesA[0].to_csv('/home/sibgha/thesis-files/adidas.csv')


tablesM = camelot.read_pdf('/home/sibgha/thesis-files/merck.pdf',  flavor='stream', pages='78',  strip_text='\n')
tablesM[0].to_csv('/home/sibgha/thesis-files/merck1.csv')
tablesM[1].to_csv('/home/sibgha/thesis-files/merck2.csv')
tablesM[2].to_csv('/home/sibgha/thesis-files/merck3.csv')


tablesP = camelot.read_pdf('/home/sibgha/thesis-files/puma.pdf',  flavor='stream', pages='1-end',  strip_text='\n',  edge_tol=500, split_text=True)
tablesP[0].to_csv('/home/sibgha/thesis-files/puma1.csv')
tablesP[1].to_csv('/home/sibgha/thesis-files/puma2.csv')
tablesP[2].to_csv('/home/sibgha/thesis-files/puma3.csv')
tablesP[3].to_csv('/home/sibgha/thesis-files/puma4.csv')

tablesA = camelot.read_pdf('/home/sibgha/thesis-files/AstraZeneca.pdf',  flavor='stream', pages='85',  strip_text='\n', edge_tol=500, split_text=True)
tablesA[0].to_csv('/home/sibgha/thesis-files/AstraZeneca.csv')


tablesM = camelot.read_pdf('/home/sibgha/thesis-files/pfizer.pdf',  flavor='stream', pages='47',  strip_text='\n')
tablesM[0].to_csv('/home/sibgha/thesis-files/pfizer.csv')
tablesM[1].to_csv('/home/sibgha/thesis-files/pfizer2.csv')
  
