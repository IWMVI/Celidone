const { app , BrowserWindow , nativeTheme, Menu, ipcMain } = require('electron');
const path = require('node:path');

// Janela onde o dashboard vai ficar
const createWindow = () => {
    nativeTheme.themeSource = 'dark'
    const win = new BrowserWindow({
        width: 800,
        height: 600,
        icon: './src/public/img/bowTie2.png',
        //Mude isso para ocultar as ferramentas do menu (e também do desenvolvedor)
        autoHideMenuBar: false,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js')
        }
    })

    win.loadFile('./src/views/index.html')
}

// Janela aonde vai estar o CRUD do cliente
const clienteWindow = () => {
    const father = BrowserWindow.getFocusedWindow()
    if (father) {
        const cliente = new BrowserWindow({
            width: 800,
            height: 600,
            icon: './src/public/img/bowTie2.png',
            //Mude isso para ocultar as ferramentas do menu (e também do desenvolvedor)
            autoHideMenuBar: true,
            parent: father
        })
        cliente.loadFile('./src/views/cliente.html')
    }
}

app.whenReady().then(() => {
    createWindow()

    // IPC >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // Aqui é onde o IPC vai escutar as mensagens do processo de renderização
    ipcMain.on('open-window', () => {
        clienteWindow();
    })

    ipcMain.on('renderer-message', (event, message) => {
        console.log(`Main recebeu uma mensagem: ${message}`)
        event.reply('main-message', 'Mensagem recebida com sucesso!')
    })

    // só ir inserindo...
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) { createWindow() }
    })
})

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') { app.quit() }
})

// Template do menu
// Para caso seja necessário criar um menu customizado
// const menu = Menu.buildFromTemplate(template)

const template = [
    {
        label: 'Arquivo',
    },
    {
        label: 'Exibir',
    },
    {
        label: 'Ajuda',
    }
]