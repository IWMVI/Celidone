/**
 * Hot Reload para desenvolvimento
 * Monitora mudanças nos arquivos e recarrega automaticamente
 */

import fs from "fs";
import path from "path";

class HotReload {
    constructor(mainWindow) {
        this.mainWindow = mainWindow;
        this.watchers = new Map();
        this.debounceTimers = new Map();
        this.isEnabled =
            process.env.NODE_ENV === "development" ||
            process.argv.includes("--dev");

        if (this.isEnabled) {
            console.log("🔥 Hot Reload ativado");
            this.init();
        }
    }

    init() {
        // Monitora arquivos JavaScript
        this.watchDirectory("./src/app", [".js"], () => {
            console.log("📝 Arquivo JS modificado, recarregando...");
            this.reload();
        });

        // Monitora arquivos CSS
        this.watchDirectory("./src/public/css", [".css"], () => {
            console.log("🎨 Arquivo CSS modificado, recarregando estilos...");
            this.reloadStyles();
        });

        // Monitora arquivos HTML
        this.watchDirectory("./src/public/views", [".html"], () => {
            console.log("📄 Arquivo HTML modificado, recarregando...");
            this.reload();
        });
    }

    watchDirectory(dir, extensions, callback) {
        if (!fs.existsSync(dir)) {
            console.warn(`⚠️  Diretório não encontrado: ${dir}`);
            return;
        }

        const watcher = fs.watch(
            dir,
            { recursive: true },
            (eventType, filename) => {
                if (!filename) return;

                const ext = path.extname(filename);
                if (!extensions.includes(ext)) return;

                const fullPath = path.join(dir, filename);

                // Debounce para evitar múltiplos reloads
                const debounceKey = fullPath;
                if (this.debounceTimers.has(debounceKey)) {
                    clearTimeout(this.debounceTimers.get(debounceKey));
                }

                this.debounceTimers.set(
                    debounceKey,
                    setTimeout(() => {
                        console.log(`📁 Arquivo modificado: ${filename}`);
                        callback(fullPath);
                        this.debounceTimers.delete(debounceKey);
                    }, 100)
                );
            }
        );

        this.watchers.set(dir, watcher);
    }

    reload() {
        if (this.mainWindow && !this.mainWindow.isDestroyed()) {
            this.mainWindow.webContents.reload();
        }
    }

    reloadStyles() {
        if (this.mainWindow && !this.mainWindow.isDestroyed()) {
            // Força reload apenas dos estilos
            this.mainWindow.webContents.executeJavaScript(`
                const links = document.querySelectorAll('link[rel="stylesheet"]');
                links.forEach(link => {
                    const href = link.href.split('?')[0];
                    link.href = href + '?t=' + Date.now();
                });
                console.log("🎨 Estilos recarregados");
            `);
        }
    }

    destroy() {
        console.log("🔥 Desativando Hot Reload...");
        this.watchers.forEach((watcher) => watcher.close());
        this.watchers.clear();
        this.debounceTimers.forEach((timer) => clearTimeout(timer));
        this.debounceTimers.clear();
    }
}

export default HotReload;
