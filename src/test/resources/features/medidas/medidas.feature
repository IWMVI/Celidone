# language: pt

Funcionalidade: Gerenciamento de medidas corporais

  # ==========================
  # CRIAR MEDIDA MASCULINA
  # ==========================

  Cenario: Nao deve criar medida masculina quando cliente nao existir
    Quando envio uma requisicao de cadastro de medida masculina com os dados:
      | clienteId | cintura | manga | colarinho | barra | torax |
      | 999       | 80.00   | 60.00 | 40.00     | 50.00 | 100.00 |
    Entao o status da resposta de medida deve ser 500

  Cenario: Nao deve criar medida masculina quando medida for negativa
    Dado que existe cliente para medida com cpf "11111111113"
    E que o cliente tem uma medida masculina cadastrada
    Quando envio uma requisicao de cadastro de medida masculina com os dados:
      | clienteId | cintura | manga | colarinho | barra | torax |
      | 1         | -10.00  | 60.00 | 40.00     | 50.00 | 100.00 |
    Entao o status da resposta de medida deve ser 400

  # ==========================
  # CRIAR MEDIDA FEMININA
  # ==========================

  Cenario: Nao deve criar medida feminina quando cliente nao existir
    Quando envio uma requisicao de cadastro de medida feminina com os dados:
      | clienteId | cintura | manga | alturaBusto | raioBusto | corpo | ombro | decote | quadril | comprimentoVestido |
      | 999       | 70.00   | 55.00 | 90.00       | 18.00     | 45.00 | 38.00 | 15.00  | 95.00   | 110.00             |
    Entao o status da resposta de medida deve ser 500

  # ==========================
  # BUSCAR MEDIDA POR ID
  # ==========================

  Cenario: Deve buscar medida masculina por id existente
    Dado que existe cliente para medida com cpf "33333333333"
    E que o cliente tem uma medida masculina cadastrada
    Quando envio uma requisicao de busca da medida masculina
    Entao o status da resposta de medida deve ser 200
    E o campo de medida "sexo" da resposta deve ser "MASCULINO"

  Cenario: Deve buscar medida feminina por id existente
    Dado que existe cliente para medida com cpf "33333333334"
    E que o cliente tem uma medida feminina cadastrada
    Quando envio uma requisicao de busca da medida feminina
    Entao o status da resposta de medida deve ser 200
    E o campo de medida "sexo" da resposta deve ser "FEMININO"

  Cenario: Deve retornar erro ao buscar medida inexistente
    Quando envio uma requisicao de busca da medida por id 999999
    Entao o status da resposta de medida deve ser 500

  # ==========================
  # LISTAR MEDIDAS
  # ==========================

  Cenario: Deve retornar lista vazia quando nao existirem medidas
    Dado que existe cliente para medida com cpf "44444444444"
    Quando envio uma requisicao de listagem de medidas
    Entao o status da resposta de medida deve ser 200
    E a resposta de medidas deve ser uma lista vazia

  Cenario: Deve listar todas as medidas quando existirem
    Dado que existe cliente para medida com cpf "44444444445"
    E que o cliente tem uma medida masculina cadastrada
    E que o cliente tem uma medida feminina cadastrada
    Quando envio uma requisicao de listagem de medidas
    Entao o status da resposta de medida deve ser 200
    E a resposta deve conter 2 medidas

  Cenario: Deve filtrar medidas por cliente
    Dado que existe cliente para medida com cpf "44444444446"
    E que o cliente tem uma medida masculina cadastrada
    Quando listo as medidas do cliente
    Entao o status da resposta de medida deve ser 200
    E a resposta deve conter 1 medidas

  Cenario: Deve filtrar medidas por sexo
    Dado que existe cliente para medida com cpf "44444444447"
    E que o cliente tem uma medida masculina cadastrada
    Quando listo as medidas por sexo "MASCULINO"
    Entao o status da resposta de medida deve ser 200

  # ==========================
  # ATUALIZAR MEDIDA
  # ==========================

  Cenario: Deve atualizar medida masculina quando dados forem validos
    Dado que existe cliente para medida com cpf "55555555555"
    E que o cliente tem uma medida masculina cadastrada
    Quando atualizo a medida masculina com os dados:
      | cintura | manga | colarinho | barra | torax |
      | 85.00   | 62.00 | 42.00     | 52.00 | 105.00 |
    Entao o status da resposta de medida deve ser 200
    E o campo de medida "cintura" da resposta deve ser "85.0"

  Cenario: Deve atualizar medida feminina quando dados forem validos
    Dado que existe cliente para medida com cpf "55555555556"
    E que o cliente tem uma medida feminina cadastrada
    Quando atualizo a medida feminina com os dados:
      | cintura | manga | alturaBusto | raioBusto | corpo | ombro | decote | quadril | comprimentoVestido |
      | 72.00   | 57.00 | 92.00       | 19.00     | 47.00 | 40.00 | 16.00  | 97.00   | 115.00             |
    Entao o status da resposta de medida deve ser 200

  Cenario: Deve retornar erro ao atualizar medida inexistente
    Quando atualizo a medida masculina inexistente com os dados:
      | cintura | manga | colarinho | barra | torax |
      | 85.00   | 62.00 | 42.00     | 52.00 | 105.00 |
    Entao o status da resposta de medida deve ser 500

  # ==========================
  # DELETAR MEDIDA
  # ==========================

  Cenario: Deve deletar medida masculina quando existir
    Dado que existe cliente para medida com cpf "66666666666"
    E que o cliente tem uma medida masculina cadastrada
    Quando deleto a medida masculina
    Entao o status da resposta de medida deve ser 204
    E a medida nao deve mais existir

  Cenario: Deve deletar medida feminina quando existir
    Dado que existe cliente para medida com cpf "66666666667"
    E que o cliente tem uma medida feminina cadastrada
    Quando deleto a medida feminina
    Entao o status da resposta de medida deve ser 204
    E a medida nao deve mais existir

  Cenario: Deve retornar erro ao deletar medida inexistente
    Quando deleto a medida com id 999999
    Entao o status da resposta de medida deve ser 500
