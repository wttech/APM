module.exports = function (grunt) {
    require('matchdep').filterAll('grunt-*').forEach(grunt.loadNpmTasks);

    var pkgJson = require('./package.json');
    var releaseDirectory = 'wiki/releases/' + pkgJson.version;
    var releaseFile = releaseDirectory + '/Documentation-' + pkgJson.version + '.md';

    grunt.initConfig({
        copy: {
            assets: {
                expand: true,
                cwd: 'wiki',
                src: 'assets/**',
                dest: releaseDirectory
            }
        },
        json_generator: {
            markdown: {
                dest: 'markdown.json',
                options: {
                    build: releaseFile,
                    files: [ 'DocumentationTemplate.md' ]
                }
            }
        }
    });

    grunt.registerTask('markdown-compile', 'Compiles markdown', function() {
        var done = this.async();

        var fs = require('fs-extra');

        /* This is synchronous function that checks whether specified directory exists and creates it if it
           doesn't exist. It is required here since the markdown-include package produces error if target
           directory doesn't exist.
        */
        fs.ensureDirSync(releaseDirectory);

        var markdownInclude = require('markdown-include');
        markdownInclude.compileFiles('markdown.json').then(function (data) {
            done();
        });
        setTimeout(function() {
            done();
        }, 5000);
    });

    grunt.registerTask('build', ['json_generator', 'markdown-compile', 'copy']);
};
