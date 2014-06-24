function display_stage(Z, n, dim)
% A function that draws the puppet stage from the top down.
% Z - cell of puppet states (x,y)
%       i.e. for two puppets, Z{1} = [x1 y1]
% n - # of puppets
% dim - stage dimensions
%       i.e. 1x3, 2x2, etc. (max  = 3x3!)

eval('plot_data');

%% draw the basic stage
draw_borders(dim);

%% draw the puppet trajectories
% create puppet handles
P = [];
for i = 1 : n
    run_length = length(Z{i});
    % grab data from Z cell
    z = Z{i};
    % scale to centimeters
    z = 100.*z;
    % draw trajectory
    x_traj = z(1,:);
    y_traj = z(2,:);
    plot(x_traj, y_traj, colors(i));
    %draw puppet at last position
    x = z(1,run_length)-puppet_diam/2;
    y = z(2,run_length)-puppet_diam/2;
    P(i) = rectangle('Position',[x,y,puppet_diam,puppet_diam],'Curvature',[1,1],'FaceColor',colors(i));
end
