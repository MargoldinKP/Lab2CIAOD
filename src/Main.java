import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.BiFunction;
import java.util.HashMap;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        int ans;
        String[] postfixExspression, infixExspression;

        do {
            Scanner in = new Scanner(System.in);
            System.out.println("Меню:");
            System.out.println("1) Вычисление инфиксного выражения");
            System.out.println("2) Вычисленеи постфиксного выражения");
            System.out.println("3) Перевод из инфиксной формы в постфиксную");
            System.out.println("4) Выход\n");
            System.out.print("Выберете пунк меню: ");
            ans = in.nextInt();
            switch (ans) {
                case 1:
                    System.out.print("Введите инфиксное выражение(каждая цифра и символ через пробел): ");
                    in.nextLine(); //очищаем буфер
                    String inFixString = in.nextLine(); //считываем строку
                    infixExspression = inFixString.split(" ");//разбиваем на слова
                    convFunction(infixExspression);
                    System.out.println("Ответ " + calcInfix(infixExspression));
                    break;
                case 2:
                    System.out.print("Введите постфиксное выражение(каждая цифра и символ через пробел): ");
                    in.nextLine(); // очищаем буфер
                    String postfixstr = in.nextLine();
                    postfixExspression = postfixstr.split(" ");
                    convFunction(postfixExspression);
                    System.out.println("Ответ " + calcPostfix(postfixExspression));
                    break;
                case 3:
                    System.out.print("Введите инфиксное выражение(каждая цифра и символ через пробел): ");
                    in.nextLine(); // очищаем буфер
                    String infixstr = in.nextLine();
                    infixExspression = infixstr.split(" ");
//                    convFunction(infixExspression);
                    System.out.println("Результат: " + convInFixToPostFix(infixExspression));
                    break;
            }
        } while (ans != 4);
    }

    //функция с заменой значениями
    public static void convFunction(String[] queue) {
        String regular = "(sin|cos|tg|ctg|exp)\\(([^)]+)\\)";
        Pattern pattern = Pattern.compile(regular);

        for (int i = 0; i < queue.length; i++) {
            Matcher matcher = pattern.matcher(queue[i]);
            while (matcher.find()) {
                String function = matcher.group(1);
                String innerExpression = matcher.group(2);

                // Преобразование строки в инфиксной форме в постфиксную.
                String[] tokens = tokenize(innerExpression);
                innerExpression =  convInFixToPostFix(tokens);

                String[] postfixArray = innerExpression.split(" ");
                double evaluatedValue = calcPostfix(postfixArray);
                double functionResult = calculateFunction(function, evaluatedValue);
                queue[i] = String.valueOf(functionResult);
            }
        }
    }

    private static String[] tokenize(String expression) {
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // если текущий символ является частью числа (цифра или точка) или оператором
            if (Character.isDigit(ch) || ch == '.' || isOperator(ch)) {
                tokens.add(Character.toString(ch)); // Добавляем каждый символ как отдельный токен
            }
        }

        // возвращаем полученные токены в виде массива
        return tokens.toArray(new String[0]);
    }

    // метод для проверки, является ли символ оператором
    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
    }

    private static double calculateFunction(String function, double argument) {
        // Вычисление тригонометрических и экспоненциальной функций.
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(argument));
            case "cos":
                return Math.cos(Math.toRadians(argument));
            case "tg":
                return Math.tan(Math.toRadians(argument));
            case "ctg":
                return 1.0 / Math.tan(Math.toRadians(argument));
            case "exp":
                return Math.exp(argument);
            default:
                throw new IllegalArgumentException("Неизвестная функция: " + function);
        }
    }

    public static double applyOperation(double operand1, double operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ArithmeticException("Нельзя делить на 0");
                }
                return operand1 / operand2;
            case "^":
                return Math.pow(operand1, operand2);
            default:
                throw new IllegalArgumentException("Неверный оператор: " + operator);
        }
    }

    public static int priority(String operator) {
        switch (operator) {
            case "^":
                return 3;
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    // Метод для вычисления инфиксного выражения
    public static double calcInfix(String[] tokens) {
        Stack<Double> operands = new Stack<>(); // Стек операндов
        Stack<String> operators = new Stack<>(); // Стек операторов
        Map<String, BiFunction<Double, Double, Double>> operationMap = new HashMap<>(); // Словарь операций
        // Заполнение словаря операций
        operationMap.put("+", (a, b) -> a + b);
        operationMap.put("-", (a, b) -> a - b);
        operationMap.put("*", (a, b) -> a * b);
        operationMap.put("/", (a, b) -> a / b);

        Map<String, Integer> precedenceMap = new HashMap<>(); // Словарь приоритетов операций
        // Заполнение словаря приоритетов операций
        precedenceMap.put("+", 1);
        precedenceMap.put("-", 1);
        precedenceMap.put("*", 2);
        precedenceMap.put("/", 2);
        precedenceMap.put("^", 3);

        for (String token : tokens) {
            if (token.matches("\\-?\\d+(\\.\\d+)?")) { // Если токен - число
                operands.push(Double.parseDouble(token));
            } else if (token.equals("(")) { // Если токен - открывающая скобка
                operators.push(token);
            } else if (token.equals(")")) { // Если токен - закрывающая скобка
                while (!operators.peek().equals("(")) {
                    if (operators.isEmpty()) {
                        throw new IllegalStateException("Некорректное количество скобок");
                    }
                    operands.push(applyOperation(operands.pop(), operands.pop(), operators.pop(), operationMap));
                }
                operators.pop();
            } else if (precedenceMap.containsKey(token)) { // Если токен - оператор
                while (!operators.isEmpty() && precedenceMap.get(token) <= precedenceMap.get(operators.peek())) {
                    operands.push(applyOperation(operands.pop(), operands.pop(), operators.pop(), operationMap));
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) { // выполнение оставшихся операций
            operands.push(applyOperation(operands.pop(), operands.pop(), operators.pop(), operationMap));
        }

        return operands.pop(); // возвращение результата
    }

    // Метод для применения операции к операндам
    private static double applyOperation(double operand2, double operand1, String operator,
                                         Map<String, BiFunction<Double, Double, Double>> operationMap) {
        if (!operationMap.containsKey(operator)) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
        return operationMap.get(operator).apply(operand1, operand2);
    }

    // Метод для вычисления постфиксного выражения
    public static double calcPostfix(String[] queue) {
        Stack<Double> stack = new Stack<>(); // стек для выполнения операций
        for (String item : queue) {
            if (item.matches("\\-?\\d+(\\.\\d+)?")) { // если элемент - число
                stack.push(Double.parseDouble(item));
            } else if (stack.size() < 2) { // если в стеке недостаточно операндов
                throw new IllegalArgumentException("Недостаточно операндов в стеке для операции " + item);
            } else { // если элемент - оператор
                Double operand2 = stack.pop();
                Double operand1 = stack.pop();
                double result = applyOperation(operand1, operand2, item);
                stack.push(result);
            }
        }
        if (stack.size() != 1) { // проверка на корректность вычислений
            throw new IllegalStateException("В стеке должен остаться один элемент после вычисления");
        }
        return stack.pop(); // возвращение результата
    }

    public static String convInFixToPostFix(String[] infixTokens) {
        Stack<String> operators = new Stack<>();
        Queue<String> postfix = new LinkedList<>();

        for (String token : infixTokens) {
            // проверяем, является ли токен числом или функцией с аргументами
            if (token.matches("\\-?\\d+(\\.\\d+)?") || token.matches("[a-zA-Z]+\\(.*?\\)")) {
                postfix.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    postfix.add(operators.pop());
                }
                operators.pop(); // удаление '(' из стека
            } else {
                // управление приоритетом операторов
                while (!operators.isEmpty() && priority(operators.peek()) >= priority(token)) {
                    postfix.add(operators.pop());
                }
                operators.push(token); // добавляем оператор в стек
            }
        }

        while (!operators.isEmpty()) { // добавляем оставшиеся операторы из стека в выходную очередь
            postfix.add(operators.pop());
        }

        return String.join(" ", postfix); // возвращаем постфиксное выражение в виде строки
    }

}

