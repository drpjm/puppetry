function draw_borders(dim)
% function that draws lines to divide the stage into regions

eval('plot_data');

% y axis - dim(1); x axis dim(2)
size_xaxis = dim(2)*length_per_unit;
size_yaxis = dim(1)*length_per_unit;

figure(99);
hold on;

%horizontal region divider
for i = 1 : dim(1)
    if(i ~= 1)
        horiz_line = 0:dx:size_xaxis;
        vertical_pos = (i-1)*length_per_unit*ones(1,length(horiz_line));
        plot(horiz_line, vertical_pos, 'k--');
    end
end

%vertical region divider
for j = 1 : dim(2)
    if (j ~= 1)
        vert_line = 0:dx:size_yaxis;
        horizontal_pos = (j-1)*length_per_unit*ones(1,length(vert_line));
        plot(horizontal_pos, vert_line, 'k--');
    end
end

axis([0 size_xaxis 0 size_yaxis]);
