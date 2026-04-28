package test

class Parent {

    String name
    static searchable = { children component: true }
    static hasMany = [children: Child]

}
