const { contextBridge, ipcRenderer } = require('electron');

// Processos criados
contextBridge.exposeInMainWorld('api', {
    
    // Mostra a versão do Electron
    verElectron: () => process.versions.electron,
    // Abre uma janela nova com um parâmetro
    open: (param) => ipcRenderer.send('open-window', param),

    // Troca de mensagens
    send: (message) => ipcRenderer.send('renderer-message', message),
    on: (message) => ipcRenderer.on('main-message', message)


})


 
//window.addEventListener('DOMContentLoaded', () => {
//    const janelaCliente = document.getElementById('btn-clientes').innerHTML = clienteWindow()
//})