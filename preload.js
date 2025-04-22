const { contextBridge, ipcRenderer } = require('electron');

// Processos criados
contextBridge.exposeInMainWorld('api', {
    verElectron: () => process.versions.electron,
    open: () => ipcRenderer.send('open-window'),
    send: (message) => ipcRenderer.send('renderer-message', message),
    on: (message) => ipcRenderer.on('main-message', message)
})


 
//window.addEventListener('DOMContentLoaded', () => {
//    const janelaCliente = document.getElementById('btn-clientes').innerHTML = clienteWindow()
//})