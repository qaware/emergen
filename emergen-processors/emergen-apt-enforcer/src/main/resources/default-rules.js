var enforce = function (annotation, element) {
    if (annotation.getQualifiedName() == "de.qaware.emergen.apt.enforcer.EnforcerSupport") {
        print("Validating element " + element);
        return true;
    } else {
        print("Unsupported annotation " + annotation);
        return false;
    }
};