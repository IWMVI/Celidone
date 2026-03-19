# language: pt

Funcionalidade: Gerenciamento de clientes

  # ==========================
  # CRIAR
  # ==========================

  Esquema do Cenario: Nao deve criar cliente quando campo obrigatorio estiver ausente
    Dado que nao existe cliente com cpf "<cpfCnpj>"
    Quando envio uma requisicao de cadastro com os dados:
      | nome     | cpfCnpj     | email       | celular     | cep       | logradouro     | numero | cidade       | bairro | estado | complemento |
      | <nome>  | <cpfCnpj>   | <email>     | <celular>   | <cep>     | <logradouro>   | <numero> | <cidade>    | <bairro> | <estado> | <complemento> |
    Entao o status da resposta deve ser 400
    E o campo "message" da resposta deve conter "<mensagem>"

    Exemplos:
      | nome         | cpfCnpj       | email           | celular      | cep       | logradouro     | numero | cidade      | bairro | estado | complemento | mensagem               |
      |             | 12345678901   | a@email.com     | 11999999999  | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    | Nome é obrigatório     |
      | Wallace     |               | a@email.com     | 11999999999  | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    | CPF é obrigatório      |
      | Wallace     | 12345678901   | a@email.com     |              | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    | Celular é obrigatório |
      | Wallace     | 12345678901   |                 | 11999999999  | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    | Email é obrigatório    |
      | Wallace     | 12345678901   | a@email.com     | 11999999999  |           |                |        |            |        |        |             | Endereço é obrigatório |

  Cenario: Deve criar cliente quando todos os dados sao validos
    Dado que nao existe cliente com cpf "12345678901"
    Quando envio uma requisicao de cadastro com os dados:
      | nome          | cpfCnpj       | email           | celular     | cep       | logradouro     | numero | cidade      | bairro | estado | complemento |
      | João da Silva | 12345678901   | joao@email.com  | 11999999999 | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    |
    Entao o status da resposta deve ser 200
    E deve existir um cliente com cpf "12345678901"
    E o campo "cpfCnpj" da resposta deve ser "12345678901"
    E o campo "nome" da resposta deve ser "João da Silva"

  Cenario: Nao deve criar cliente com CPF ja cadastrado
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de cadastro com os dados:
      | nome             | cpfCnpj       | email             | celular     | cep       | logradouro     | numero | cidade      | bairro | estado | complemento |
      | Empresa XPTO LTDA | 12345678901   | empresa@email.com | 11988888888 | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF já cadastrado"

  # ==========================
  # LISTAR
  # ==========================

  Cenario: Deve retornar lista vazia quando nenhum cliente estiver cadastrado
    Dado que nao existe nenhum cliente cadastrado
    Quando envio uma requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve ser uma lista vazia

  Cenario: Deve retornar todos os clientes cadastrados
    Dado que os seguintes clientes estao cadastrados:
      | nome           | cpfCnpj       | email          | celular     | cep       | logradouro     | numero | cidade      | bairro | estado | complemento |
      | João da Silva  | 12345678901   | joao@email.com | 11999999999 | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    |
      | Empresa XPTO   | 12345678000195| empresa@email.com | 11988888888 | 01001000  | Praça da Sé  | 100    | São Paulo  | Sé     | SP     | Sala 101    |
    Quando envio uma requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve conter 2 clientes

  # ==========================
  # BUSCAR POR ID
  # ==========================

  Cenario: Deve retornar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de busca pelo id do cliente com cpf "12345678901"
    Entao o status da resposta deve ser 200
    E o campo "cpfCnpj" da resposta deve ser "12345678901"

  Cenario: Deve retornar erro quando buscar cliente por ID inexistente
    Quando envio uma requisicao de busca pelo id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  # ==========================
  # ATUALIZAR
  # ==========================

  Cenario: Deve atualizar cliente quando dados forem validos e CPF nao mudar
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome              | cpfCnpj       | email                  | celular     | cep       | logradouro        | numero | cidade       | bairro | estado | complemento |
      | Cliente Atualizado | 12345678901   | cliente.atualizado@email.com | 11977777777 | 20040002 | Rua da Assembleia | 200    | Rio de Janeiro | Centro | RJ     | Apto 502    |
    Entao o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Cliente Atualizado"
    E o campo "email" da resposta deve ser "cliente.atualizado@email.com"

  Cenario: Deve atualizar cliente quando novo CPF for unico
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    E que nao existe cliente com cpf "00000000001"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome           | cpfCnpj       | email        | celular     | cep       | logradouro     | numero | cidade      | bairro | estado | complemento |
      | Wallace        | 00000000001   | w@email.com  | 11999999999 | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    |
    Entao o status da resposta deve ser 200
    E o campo "cpfCnpj" da resposta deve ser "00000000001"

  Cenario: Nao deve atualizar cliente quando novo CPF ja pertencer a outro cliente
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    E que ja existe um cliente cadastrado com cpf "12345678000195"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome           | cpfCnpj       | email        | celular     | cep       | logradouro     | numero | cidade      | bairro | estado | complemento |
      | Wallace        | 12345678000195| w@email.com  | 11999999999 | 01001000  | Praça da Sé    | 100    | São Paulo  | Sé     | SP     | Sala 101    |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF já cadastrado"

  Cenario: Deve retornar erro ao atualizar cliente com ID inexistente
    Quando envio uma requisicao de atualizacao do id 999999 com os dados:
      | nome           | cpfCnpj       | email        | celular     | cep       | logradouro | numero | cidade | bairro | estado | complemento |
      | Fantasma       | 11111111111   | x@email.com  | 11999999999 | 00000000  | Rua X      | 0      | Cidade X | Bairro X | XX     | Apto 0 |
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  # ==========================
  # DELETAR
  # ==========================

  Cenario: Deve deletar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de exclusao do cliente com cpf "12345678901"
    Entao o status da resposta deve ser 204
    E nao deve existir cliente com cpf "12345678901"

  Cenario: Deve retornar erro ao deletar cliente com ID inexistente
    Quando envio uma requisicao de exclusao do id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"
