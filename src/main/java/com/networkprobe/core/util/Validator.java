package com.networkprobe.core.util;

import com.networkprobe.core.exception.InvalidPropertyException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Validator {

    public static final String IPV4_REGEX_PATTERN =
            "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$";

    /* IPV4 ONLY */
    public static final String CIDR_NOTATION_PATTERN =
            "^((?:\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(?:\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])(?:/(?:[1-9]|[1-2]\\d|3[0-2]))$";

    public static String checkIsAValidIpv4(String address, String name) {
        if (!Pattern.matches(IPV4_REGEX_PATTERN, checkIsNullOrEmpty(address, name)))
            throw new InvalidPropertyException( format("\"%s\" não é um endereço Ipv4 válido.", address) );
        return address;
    }

    public static String checkCidrNotation(String cidrNotation) {
        if (!Pattern.matches(CIDR_NOTATION_PATTERN, checkIsNullOrEmpty(cidrNotation, "cidr")))
            throw new InvalidPropertyException( format("O valor \"%s\" não é um CIDR válido.", cidrNotation) );
        return cidrNotation;
    }

    public static int checkIsPositive(int number, String name) {
        if (number < 0)
            throw new InvalidPropertyException( format("A propriedade \"%s\" não pode ser negativa.", name) );
        return number;
    }

    public static int checkIsLowerThan(int number, int lower, String name) {
        if(number < lower)
            throw new InvalidPropertyException( format("A propriedade \"%s\" não pode ser menor que %d.", name, lower) );
        return number;
    }

    public static int checkBounds(int number, int min, int max, String name) {
        if(number < min || number > max)
            throw new InvalidPropertyException( format("A propriedade \"%s\" não pode ser menor que %d ou " +
                    "maior que %d.", name, min, max) );
        return number;
    }

    public static File checkIsReadable(File file, String name) {
        checkIfExists(file, name);
        if (!file.canRead())
            throw new SecurityException( format("Não é possível ler o arquivo \"%s\".", name) );
        return file;
    }

    public static File checkIfExists(File file, String name) {
        checkIsNotNull(file, name);
        if (!file.exists())
            throw new NullPointerException( format("O arquivo \"%s\" não existe.", name) );
        return file;
    }

    public static <E> E checkIsNotNull(E object, String name) {
        if (object == null)
            throw new NullPointerException( format("O campo \"%s\" não pode ser nulo.", name) );
        return object;
    }

    public static String checkIsNullOrEmpty(String str, String name) {
        if (str == null || str.isEmpty())
            throw new NullPointerException( format("O valor de texto \"%s\" é nulo ou vazio.", name) );
        return str;
    }

}
