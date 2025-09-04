Projekt je sestavljen iz treh glavnih delov. Ti deli morajo delovati posamično, kot tudi integritetno, 
da je pričakovan rezultat projekta (mobilna igra) uporaben in praktičen.


1.) back-end 
Zaledni del je sestavljen iz "server.py" in "database_main.py". V prvei datoteki imamo deklarirane glavne API klice in sicer GET, POST, PUT in DELETE.



######  GET  #######
@app.get("/")   # MAIN  index  PAGE     
async def root():
    return FileResponse('index.html', media_type='text/html')



2.)
