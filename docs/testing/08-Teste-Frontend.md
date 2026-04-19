# Testes Frontend

A documentação completa de testes do frontend está no repositório `TCC-Frontend`:

```
TCC-Frontend/docs/testing/
├── README.md                ← Visão geral e início rápido
├── COMO-CRIAR-TESTES.md     ← Guia passo a passo
├── 01-Arquitetura-Testes.md ← Estrutura, convenções e tipos de teste
├── 02-Teste-Componente.md   ← Testes de componentes React com RTL
├── 03-Teste-Hook.md         ← Testes de custom hooks
├── 04-Teste-Servico.md      ← Testes de serviços e chamadas de API
└── 05-Melhores-Praticas.md  ← Convenções, anti-padrões e checklist
```

## Stack do Frontend

| Ferramenta | Propósito |
|---|---|
| **Jest** | Framework de teste |
| **ts-jest** | Suporte a TypeScript |
| **React Testing Library** | Teste de componentes React |
| **@testing-library/user-event** | Simulação de interações do usuário |
| **jsdom** | Ambiente DOM simulado |

## Executando os Testes do Frontend

```bash
# Na pasta TCC-Frontend
npm test

# Com cobertura
npm run test:coverage
```

## Veja Também

- [Arquitetura de Testes Backend](01-Arquitetura-Testes.md)
- [Integração CI/CD](10-Integracao-CI-CD.md)
