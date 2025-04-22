/**
 * Processo de renderização do Electron
 */

console.log('Processo de renderização iniciado!')
console.log(`Versão do Electron: ${api.verElectron()}`);

/**
 * Basicamente, as coisas aqui vão utilizar o IPC para se comunicar com o main,
 * e lá tem os processos de abrir janelas, etc..
 */

function janelaCliente() {
    //console.log('Janela de cliente aberta!')
    api.open();
}

api.send("Message testing");
api.on((event, message) => {
    console.log(`Processo de renderização recebeu uma mensagem: ${message}`)
})