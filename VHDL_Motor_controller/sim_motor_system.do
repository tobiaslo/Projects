vsim -voptargs=+acc work.tb_motor_system
add wave sim:/tb_motor_system/tb_clk
add wave sim:/tb_motor_system/tb_dir
add wave sim:/tb_motor_system/tb_dir_synch
add wave sim:/tb_motor_system/tb_duty_cycle
add wave sim:/tb_motor_system/tb_en
add wave sim:/tb_motor_system/tb_en_synch
add wave sim:/tb_motor_system/tb_reset

run 1 ms
