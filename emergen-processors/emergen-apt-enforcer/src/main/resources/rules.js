var enforce = function (annotation, element) {
    if (annotation.getQualifiedName() == "de.qaware.emergen.apt.enforcer.EnforcerSupport") {
        print(element);
        return true;
    } else {
        return false;
    }
};