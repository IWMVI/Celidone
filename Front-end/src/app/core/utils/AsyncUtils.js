/**
 * Utilitário para operações assíncronas
 * Single Responsibility: Apenas operações relacionadas a promises e async/await
 */
class AsyncUtils {
    /**
     * Executa função com retry em caso de falha
     * @param {Function} fn - Função a ser executada
     * @param {number} maxRetries - Número máximo de tentativas
     * @param {number} delay - Delay entre tentativas (ms)
     * @returns {Promise} Resultado da função
     */
    static async withRetry(fn, maxRetries = 3, delay = 1000) {
        let lastError;

        for (let i = 0; i <= maxRetries; i++) {
            try {
                return await fn();
            } catch (error) {
                lastError = error;

                if (i === maxRetries) {
                    throw lastError;
                }

                await this.delay(delay * (i + 1)); // Backoff exponencial
            }
        }
    }

    /**
     * Cria delay assíncrono
     * @param {number} ms - Tempo em milissegundos
     * @returns {Promise} Promise que resolve após o delay
     */
    static delay(ms) {
        return new Promise((resolve) => setTimeout(resolve, ms));
    }

    /**
     * Executa função com timeout
     * @param {Function} fn - Função a ser executada
     * @param {number} timeout - Timeout em milissegundos
     * @returns {Promise} Resultado da função ou erro de timeout
     */
    static async withTimeout(fn, timeout) {
        const timeoutPromise = new Promise((_, reject) => {
            setTimeout(() => reject(new Error("Timeout")), timeout);
        });

        return Promise.race([fn(), timeoutPromise]);
    }

    /**
     * Debounce para funções assíncronas
     * @param {Function} fn - Função a ser debounced
     * @param {number} delay - Delay do debounce
     * @returns {Function} Função debounced
     */
    static debounce(fn, delay) {
        let timeoutId;

        return (...args) => {
            clearTimeout(timeoutId);

            return new Promise((resolve, reject) => {
                timeoutId = setTimeout(async () => {
                    try {
                        const result = await fn(...args);
                        resolve(result);
                    } catch (error) {
                        reject(error);
                    }
                }, delay);
            });
        };
    }

    /**
     * Throttle para funções assíncronas
     * @param {Function} fn - Função a ser throttled
     * @param {number} delay - Delay do throttle
     * @returns {Function} Função throttled
     */
    static throttle(fn, delay) {
        let lastCall = 0;

        return async (...args) => {
            const now = Date.now();

            if (now - lastCall >= delay) {
                lastCall = now;
                return await fn(...args);
            }
        };
    }

    /**
     * Executa múltiplas promises em paralelo com limite
     * @param {Array<Function>} tasks - Array de funções que retornam promises
     * @param {number} limit - Limite de execuções simultâneas
     * @returns {Promise<Array>} Array com resultados
     */
    static async parallelLimit(tasks, limit) {
        const results = [];
        const executing = [];

        for (let i = 0; i < tasks.length; i++) {
            const task = tasks[i];
            const promise = task().then((result) => {
                results[i] = result;
                return result;
            });

            executing.push(promise);

            if (executing.length >= limit) {
                await Promise.race(executing);
                executing.splice(
                    executing.findIndex((p) => p === promise),
                    1
                );
            }
        }

        await Promise.all(executing);
        return results;
    }

    /**
     * Converte callback para Promise
     * @param {Function} fn - Função com callback
     * @returns {Function} Função que retorna Promise
     */
    static promisify(fn) {
        return (...args) => {
            return new Promise((resolve, reject) => {
                fn(...args, (error, result) => {
                    if (error) {
                        reject(error);
                    } else {
                        resolve(result);
                    }
                });
            });
        };
    }
}

export default AsyncUtils;
