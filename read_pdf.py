import sys, io, os, glob
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
import pdfplumber
files = glob.glob(r'd:\Projects\sanpham\*.pdf')
print('Found files:', files)
if files:
    with pdfplumber.open(files[0]) as pdf:
        for i, page in enumerate(pdf.pages):
            print(f'=== TRANG {i+1} ===')
            text = page.extract_text()
            if text:
                print(text)
            print()
