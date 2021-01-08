在Transformer类中实现6个方法，具体如下

1. 

``` java
 public String intToBinary(String numStr) 
```

将整数字面值（十进制表示）转化成补码表示的二进制，默认长度32位

2.

``` java
 public String binaryToInt(String binStr)
```

将补码表示的二进制转化成整数字面值（十进制表示）

3.

```java
public String floatToBinary(String floatStr)
```

将浮点数字面值转化成32位单精度浮点数表示

- 负数以"-"开头，正数不需要正号
- 考虑正负无穷的溢出（"+Inf", "-Inf"，见测试用例格式）

4.

```java
public String binaryToFloat(String binStr)
```

将32位单精度浮点数表示转化成浮点数字面值

- 特殊情况同上

5. 

``` java
public String decimalToNBCD(String decimal)
```

将十进制整数的字面值转化成NBCD表示（符号位用4位表示）

6. 

``` java
public String NBCDToDecimal(String NBCDStr)
```

将NBCD表示（符号位用4位表示）转化成十进制整数的字面值





```java
String div(String a, String b) {
    if(b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO) && !a.equals(IEEE754Float.P_ZERO)) throw new ArithmeticException();
    if((b.equals(IEEE754Float.P_ZERO) || b.equals(IEEE754Float.N_ZERO)) && (a.equals(IEEE754Float.P_ZERO) || a.equals(IEEE754Float.N_ZERO))) return IEEE754Float.NaN;

    Transformer t = new Transformer();
    myALU alu = new myALU();

    String sign;
    int exp;
    String value;

    String signA = String.valueOf(a.charAt(0));
    String signB = String.valueOf(b.charAt(0));
    int expA = Integer.parseInt(t.binaryToInt(a.substring(1,9))) - 127;
    int expB = Integer.parseInt(t.binaryToInt(b.substring(1,9))) - 127;
    String valueA = "01" + a.substring(9) + "0000" + IEEE754Float.P_ZERO;
    String valueB = IEEE754Float.P_ZERO + "01" + b.substring(9) + "0000";

    exp = expA - expB;
    String expString = t.intToBinary(String.valueOf(exp + 127)).substring(24);

    if(exp >= 127){
        if(a.charAt(0) != b.charAt(0)) return IEEE754Float.N_INF;
        else return IEEE754Float.P_INF;
    }
    else if(exp < -127){
        if(a.charAt(0) != b.charAt(0)) return IEEE754Float.N_ZERO;
        else return IEEE754Float.P_ZERO;
    }


    value = alu.div(valueA, valueB);
    for(int i = 0; i < value.length(); ++i){
        if(value.charAt(i) != '0'){
            value = value.substring(i + 1);
            break;
        }
    }

    if(!compare(a.substring(9), b.substring(9))){
        exp -= 1;
        expString = t.intToBinary(String.valueOf(exp + 127)).substring(24);
    }

    for(int i = value.length(); i < 23; ++i){
        value = value + "0";
    }
    if(value.length() >= 23) value = value.substring(0, 23);

    if(signA.equals(signB)) sign = signA;
    else sign = "1";

    return sign + expString + value;
}
```

