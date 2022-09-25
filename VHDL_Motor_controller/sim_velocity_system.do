vsim -voptargs=+acc work.tb_velocity_system
add wave sim:/tb_velocity_system/tb_sa
add wave sim:/tb_velocity_system/tb_sb
add wave sim:/tb_velocity_system/tb_sa_synch
add wave sim:/tb_velocity_system/tb_sb_synch
add wave sim:/tb_velocity_system/tb_pos_inc
add wave sim:/tb_velocity_system/tb_pos_dec
add wave sim:/tb_velocity_system/tb_velocity
add wave sim:/tb_velocity_system/tb_d0
add wave sim:/tb_velocity_system/tb_d1
add wave sim:/tb_velocity_system/tb_abcdefg
add wave sim:/tb_velocity_system/tb_c
add wave sim:/tb_velocity_system/tb_clk
add wave sim:/tb_velocity_system/tb_reset

run 100 ms
