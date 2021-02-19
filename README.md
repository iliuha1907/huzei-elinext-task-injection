# huzei-elinext-task-injection
# Java version: 14.0.1

# Описание реализации
Реализация осуществлена в классе InjectorImpl. Все привязки содеражатся в Map bindings, ключами в котором выступают элементы Class, а значениями BeanInfo. BeanInfo - 
вспомогательный класс, который хранит подходящий конструктор бина, а также информацию(флаг), является ли бин синглтоном или нет. При добавлении привязки (bind, bindSingleton)
сперва сканируются конструкторы с помощью приватного метода findAppropriateConstructor и ищется подходящий (или выбрасывается нужное исключение). Далее этот конструктор вместе с флагом вставляется в Map. Конструкторы хранятся для того, чтобы не нужно было заново сканировать тип, объект которого требуется создать.
Для хранения сущностей синглтонов используется другой Map - singletonInstances. В него добавляется запись только в случае запроса на получение соответствующего синглтона или в случае создания синглтона при иньекции.

При запросе на получение Provider в случае отсутствия в коллекциях нужного типа возвращается null, а если тип есть, то запускается цепочка создания объекта с помощью приватного
метода createInstance, где если в процессе иньекции не найден требуемый тип, то будет выброшено нужное исключение.

IncorrectInitializationException - исключение, которое оборачивает исключения, возникающие при создании объекта.

В пакетах testdao и testservice находятся сущности, используемые для тестирования.

# Сборка
Для сборки добавлен скрипт build.bat, который запускает mvn clean install.
