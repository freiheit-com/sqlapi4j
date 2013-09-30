VERSION_NUMBER = "0.2.0-SNAPSHOT"
# Group identifier for your projects
GROUP = "com.freiheit"
COPYRIGHT = "freiheit.com technologies GmbH (2012)"

# Specify Maven 2.0 remote repositories here, like this:
#repositories.remote << "http://www.ibiblio.org/maven2/"
repositories.remote << "http://repo1.maven.org/maven2/"

DEP_LOGGING = 'org.slf4j:slf4j-api:jar:1.6.1'
COMPILE_LIBS = DEP_LOGGING,"com.google.code.findbugs:jsr305:jar:1.3.9","postgresql:postgresql:jar:8.3-603.jdbc4"
TEST_LIBS = 'com.h2database:h2:jar:1.3.158'

desc "The SQL API"
define "sqlapi4j" do

    project.version = VERSION_NUMBER
    project.group = GROUP
    manifest["Implementation-Vendor"] = COPYRIGHT

    compile.options.target = '1.5'
    compile.options.source = '1.5'

    test.using :testng

    package_with_sources
    package_with_javadoc


    desc "Core"
    define :core, :base_dir => "core" do
        compile.with COMPILE_LIBS
        test.with TEST_LIBS
        package :jar
    end

    desc "Domain"
    define :domain, :base_dir => "domain" do
        compile.with COMPILE_LIBS, project(:core)
        test.with TEST_LIBS
        package :jar
    end

    desc "Dao"
    define :dao, :base_dir => "dao" do
        compile.with COMPILE_LIBS, project(:core), project(:domain)

        directory _ :target, :core
        core_test = file "target/core/test-classes" => [ project(:core)._(:target, :test, :classes), _(:target, :core) ] do |task|
            rm_f task.name
            ln_s project(:core)._(:target, :test, :classes), task.name
        end

        test.with TEST_LIBS, core_test

        package :jar
    end

    desc "Tx - Transaction Support"
    define :tx, :base_dir => "tx" do
        compile.with COMPILE_LIBS, project(:core), project(:domain)

        directory _ :target, :core
        core_test = file "target/core/test-classes" => [ project(:core)._(:target, :test, :classes), _(:target, :core) ] do |task|
            rm_f task.name
            ln_s project(:core)._(:target, :test, :classes), task.name
        end

        test.with TEST_LIBS, core_test

        package :jar
    end

end

# vim: filetype=ruby:
