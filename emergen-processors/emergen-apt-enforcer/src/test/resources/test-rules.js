var test = function (annotation, element) {
    if (element.getQualifiedName() == "de.qaware.emergen.apt.enforcer.TestBean") {
        print("Validating test element " + element);
        return false;
    } else {
        return true;
    }
};