module.exports = function(grunt) {
      // Project configuration.
      grunt.initConfig({
          pkg: grunt.file.readJSON('package.json'),
          root: '../../../',

          // Paths
          integrations: {
              less: 'resources/less/*.less',
              js: [
                    'resources/js/integrations.js',
                    'resources/js/jquery.json.js'
              ],
              controller:'controllers/IndexController.php'
          },
          temp: {
              // These are generated files. Make sure they exist before using these paths as sources
              integrations: {
                  less:      'temp/integrations.less',
                  js:        'temp/integrations.js'
              }
          },
          assets: {
              // These are generated files. Make sure they exist before using these paths as sources
              integrations: {
                  css: '<%= root %>portal/assets/integrations/css/integrations.css',
                  js:  '<%= root %>portal/assets/integrations/js/integrations.js'
              }
          },

          // Compile LESS files to CSS
          less: {
              dev: {
                  files: {
                      '<%= assets.integrations.css %>':    '<%= temp.integrations.less %>'
                  }
              },
              prod: {
                  options: {
                      'yuicompress': true
                  },
                  files: {
                      '<%= assets.integrations.css %>':    '<%= temp.integrations.less %>'
                  }
              }
          },
          // Concatinate files
          concat: {
              options: {
                  separator: ';'
              },
              dev: {
                  files: {
                      '<%= assets.integrations.js %>':    '<%= integrations.js %>'
                  }
              },
              prod: {
                  files: {
                      '<%= temp.integrations.js %>':      '<%= integrations.js %>'
                  }
              },
              less: {
                  files: {
                      '<%= temp.integrations.less %>':    [ '<%= integrations.less %>' ]
                  }
              }
          },
          // Minify and optimize JS
          closurecompiler: {
              prod: {
                  options: {
                      'compilation_level': 'SIMPLE_OPTIMIZATIONS'
                  },
                  files: {
                      '<%= assets.integrations.js %>':    '<%= temp.integrations.js %>'
                  }
              }
          },
          // Watch files for change and run grunt tasks
          watch: {
              styles: {
                  files: [
                      '<%= integrations.less %>'
                  ],
                  tasks: ['concat:less', 'less', 'clean', 'notify:build']
              },
              javascript: {
                  files: [
                      '<%= integrations.js %>'
                  ],
                  tasks: ['concat:dev', 'clean', 'notify:build']
              }
          },
          // Remove temp files
          clean: {
              build: {
                  src: [
                      'temp/'
                  ]
              }
          },
          // Reset assets in git
          shell: {
              reset: {
                  command: [
                      'git checkout -- <%= root %>portal/assets/integrations/css/integrations.css',
                      'git checkout -- <%= root %>portal/assets/integrations/js/integrations.js',
                      'git checkout -- <%= root %>bin/composer.phar'
                  ].join('&&')
              }
          },
          // Automate cache busting for prod
          'string-replace': {
              prod: {
                  options: {
                      replacements: [{
                          pattern:     /protected \$cache_version\s=\s\d+;/g,
                          replacement: 'protected $cache_version = ' + '<%= new Date().getTime() %>' + ';'
                      }]
                  },
                  files: {
                      '<%= integrations.controller %>':    '<%= integrations.controller %>'
                  }
              }
          },
          // Notify on grunt task success or fail
          notify: {
              build: {
                  options: {
                      title: 'integrations',
                      message: 'Build finished with no errors.'
                  }
              }
          },
          notify_hooks: {
              options: {
                  enabled: true,
                  title: 'integrations'
              }
          }
      });

      // LESS compiler
      grunt.loadNpmTasks('grunt-contrib-less');

      // Handlebars Compiler
      grunt.loadNpmTasks('grunt-contrib-handlebars');

      // Concatinate files
      grunt.loadNpmTasks('grunt-contrib-concat');

      // Watch files to auto run grunt tasks
      grunt.loadNpmTasks('grunt-contrib-watch');

      // Closure Compiler for JS minification/optimization
      grunt.loadNpmTasks('grunt-closurecompiler');

      // Post-build housekeeping
      grunt.loadNpmTasks('grunt-contrib-clean');

      // Git cleanup
      grunt.loadNpmTasks('grunt-shell');

      // Cache busting
      grunt.loadNpmTasks('grunt-string-replace');

      // OS notifications
      grunt.loadNpmTasks('grunt-notify');
      grunt.task.run('notify_hooks');

      // Default task
      grunt.registerTask('default', ['concat:less', 'less:dev','concat:dev', 'clean', 'notify:build']);

      // Development build
      grunt.registerTask('dev', ['default', 'watch']);

      // Production build
      grunt.registerTask('prod', ['string-replace:prod', 'concat:less','less:prod', 'concat:prod', 'closurecompiler', 'clean', 'notify:build']);

      // Clean up assets in git
      grunt.registerTask('reset', ['shell:reset']);
};