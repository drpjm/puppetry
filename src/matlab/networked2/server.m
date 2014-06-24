% Script file to invoke the PuppetServer!
disp('Invoking PuppetServer');

% grits_puppets = '/Users/pmartin/Documents/GT/projects/grits_puppets/classes';
% javolution = '/Users/pmartin/Software/JavaLibraries/javolution-5.3.1/javolution-5.3.1.jar';
grits_puppets = '/Users/grits/projects/gritspuppets/classes';
javolution = '/Users/grits/projects/gritspuppets/lib/javolution.jar';

import edu.gatech.grits.puppetctrl.matlab.*;
import edu.gatech.grits.puppetctrl.mdl.util.*;
import javolution.util.*;

javaaddpath(grits_puppets, '-end');
javaaddpath(javolution, '-end');

puppet_srv = PuppetServer;
puppet_srv.start;

disp('PuppetServer started.');