const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("api", {
    verElectron: () => process.versions.electron,
    open: (param) => ipcRenderer.send("open-window", param),
    send: (channel, data) => ipcRenderer.send(channel, data),
    on: (channel, callback) =>
        ipcRenderer.on(channel, (event, ...args) => callback(...args)),
    buscarCep: (cep) => ipcRenderer.invoke("buscar-cep", cep), // NOVO! usando ipcRenderer.invoke
});
