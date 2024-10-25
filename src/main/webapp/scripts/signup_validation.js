function isValidPassword(password) {
    var example = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!]).*$/

    return example.test(password)
}