package resource;

import exception.AppException;

public class MessageApi {

    public static String getResource(String id, Object... parameters) {
        MessageResource messageResource = getMessageResource(id);
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
            Object parameterName = parameters[parameterIndex];
            String name = "(null)";
            if (parameterName != null) {
                name = parameterName.toString();
            }
            parameterIndex++;
            if (parameterIndex >= parameters.length) {
                break;
            }
            Object value = parameters[parameterIndex];
            messageResource.setParameter(name, value);
        }
        return messageResource.getText();
    }

    public static MessageResource getMessageResource(String id) {
        MessagesResources messagesResources = MessagesResources.getInstance();
        return messagesResources.getResource(id);
    }

    public static AppException getException(String id, Object... parameters) {
        String message = getResource(id, parameters);
        return new AppException(message);
    }

    public static AppException getExceptionConcatenated(String id, Throwable e, Object... parameters) {
        String message = getResource(id, parameters);
        return new AppException(message, e);
    }
}
