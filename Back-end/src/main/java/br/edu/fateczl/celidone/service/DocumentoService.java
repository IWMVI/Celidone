package br.edu.fateczl.celidone.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentoService {

    public boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}"))
            return false;

        int soma = 0, resto;

        for (int i = 1; i <= 9; i++)
            soma += Character.getNumericValue(cpf.charAt(i - 1)) * (11 - i);
        resto = (soma * 10) % 11;
        if (resto == 10 || resto == 11)
            resto = 0;
        if (resto != Character.getNumericValue(cpf.charAt(9)))
            return false;

        soma = 0;
        for (int i = 1; i <= 10; i++)
            soma += Character.getNumericValue(cpf.charAt(i - 1)) * (12 - i);
        resto = (soma * 10) % 11;
        if (resto == 10 || resto == 11)
            resto = 0;

        return resto == Character.getNumericValue(cpf.charAt(10));
    }

    public boolean validarCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}"))
            return false;

        int[] pesos1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] pesos2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        int soma = 0;
        for (int i = 0; i < 12; i++)
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
        int resto = soma % 11;
        char dig1 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');
        if (dig1 != cnpj.charAt(12))
            return false;

        soma = 0;
        for (int i = 0; i < 13; i++)
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
        resto = soma % 11;
        char dig2 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

        return dig2 == cnpj.charAt(13);
    }
}
