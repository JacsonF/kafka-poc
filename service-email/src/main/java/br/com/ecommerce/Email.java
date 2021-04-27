package br.com.ecommerce;

public class Email {
    private final String subject, body;

    @Override
    public String toString() {
        return "Email{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public Email(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }
}
